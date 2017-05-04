package helper;

import java.util.HashMap;
import java.text.DateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mypc.inventory.Activity_Main;
import com.example.mypc.inventory.Inventory_List;
import com.example.mypc.inventory.Inventory_View;

import org.json.JSONArray;
import org.json.JSONObject;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_INV = "inventory";
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_SALES = "sales";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_COMPANY = "company";

    private static final String KEY_IDINV = "invID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_EAN = "ean";
    private static final String KEY_SUPPLIER = "supplier";
    private static final String KEY_OFFER = "offer";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_REORDER = "reorder";

    private static final String KEY_IDB = "invID";
    private static final String KEY_TITLEB = "title";
    private static final String KEY_datebought = "dateBought";
    private static final String KEY_OFFERB = "offer";
    private static final String KEY_PRICEB = "price";
    private static final String KEY_AMOUNT = "amount";

    private static final String KEY_IDS = "invID";
    private static final String KEY_TITLES = "title";
    private static final String KEY_datesold = "dateSold";
    private static final String KEY_PRICES = "price";
    private static final String KEY_AMOUNTS = "amount";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_IDINV + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_COMPANY + " TEXT" + ")";
        String CREATE_INV_TABLE = "CREATE TABLE " + TABLE_INV + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_EAN + " TEXT ," + KEY_SUPPLIER + " TEXT,"
                + KEY_OFFER + " TEXT,"
                + KEY_STOCK + " INTEGER,"
                + KEY_REORDER + " INTEGER,"
                + KEY_PRICE + " TEXT" + ")";
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_IDB + " INTEGER PRIMARY KEY," + KEY_TITLEB + " TEXT,"
                + KEY_datebought + " TEXT ,"
                + KEY_EAN + " TEXT ,"
                + KEY_OFFERB + " TEXT,"
                + KEY_AMOUNT + " TEXT,"
                + KEY_PRICEB + " TEXT" + ")";
        String CREATE_SALES_TABLE = "CREATE TABLE " + TABLE_SALES + "("
                + KEY_IDS + " INTEGER PRIMARY KEY," + KEY_TITLES + " TEXT,"
                + KEY_datesold + " TEXT ,"
                + KEY_EAN + " TEXT ,"
                + KEY_AMOUNTS + " TEXT,"
                + KEY_PRICES + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_INV_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_SALES_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String company) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_COMPANY, company); // Created At

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public boolean checkInv(String ean){
        int row = getRowCount(ean);
        if (row >= 1){
            return true;
        }
        else return false;
    }

    public boolean addSale(String title, String ean, String price, int stock){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "addSales: " + stock);
        ContentValues values = new ContentValues();
        if(getStock(ean) - stock < 0){
            return false;
        }
        else
        values.put(KEY_TITLES, title);
        values.put(KEY_EAN, ean);
        values.put(KEY_datesold, DateFormat.getDateTimeInstance().format(new Date()));
        values.put(KEY_PRICES, price);
        values.put(KEY_AMOUNTS, stock);

        long id2 = db.insert(TABLE_SALES, null, values);
        Log.d(TAG, "New sale inserted into sqlite: " + id2);

        String updateQuery = "UPDATE " + TABLE_INV +
                " set stock = stock -" + stock + " where ean = " + '"' + ean + '"';
        db.execSQL(updateQuery);
        db.close();
        return true;
    }

    public void addSales(String title, String ean, String price, String datesold, Integer stock){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "addSales: " + stock);
        ContentValues values = new ContentValues();

        values.put(KEY_TITLES, title);
        values.put(KEY_EAN, ean);
        values.put(KEY_datesold, datesold);
        values.put(KEY_PRICES, price);
        values.put(KEY_AMOUNTS, stock);

        long id2 = db.insert(TABLE_SALES, null, values);
        Log.d(TAG, "New sale inserted into sqlite: " + id2);

        db.close();
    }

    public void storeInv(String title, String ean, String supplier, String offer, String price, String stock, String reorder){
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            ContentValues values2 = new ContentValues();

            values.put(KEY_TITLE, title);
            values.put(KEY_EAN, ean);
            values.put(KEY_SUPPLIER, supplier);
            values.put(KEY_OFFER, offer);
            values.put(KEY_PRICE, price);
            values.put(KEY_STOCK, stock);
            values.put(KEY_REORDER, reorder);

            values2.put(KEY_TITLE, title);
            values2.put(KEY_EAN, ean);
            values2.put(KEY_datebought, DateFormat.getDateTimeInstance().format(new Date()));
            values2.put(KEY_OFFERB, offer);
            values2.put(KEY_PRICEB, price);
            values2.put(KEY_AMOUNT, stock);

            // Inserting Row

                long id = db.insert(TABLE_INV, null, values);
                long id2 = db.insert(TABLE_HISTORY, null, values2);
                Log.d(TAG, "New inventory inserted into sqlite: " + id);
                Log.d(TAG, "New price history inserted into sqlite: " + id2);

    }

    public void addInv(String title, String ean, String supplier, String offer, String price, int stock, int reorder) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "addInv: " + stock);
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_EAN, ean);
        values.put(KEY_SUPPLIER, supplier);
        values.put(KEY_OFFER, offer);
        values.put(KEY_PRICE, price);
        values.put(KEY_STOCK, stock);
        values.put(KEY_REORDER, reorder);

        values2.put(KEY_TITLE, title);
        values2.put(KEY_EAN, ean);
        values2.put(KEY_datebought, DateFormat.getDateTimeInstance().format(new Date()));
        values2.put(KEY_OFFERB, offer);
        values2.put(KEY_PRICEB, price);
        values2.put(KEY_AMOUNT, stock);

        // Inserting Row
        if(checkInv(ean) == false) {
            long id = db.insert(TABLE_INV, null, values);
            long id2 = db.insert(TABLE_HISTORY, null, values2);
            Log.d(TAG, "New inventory inserted into sqlite: " + id);
            Log.d(TAG, "New price history inserted into sqlite: " + id2);
        }
        else{
            long id2 = db.insert(TABLE_HISTORY, null, values2);
            Log.d(TAG, "New price history inserted into sqlite: " + id2);
            String updateQuery = "UPDATE " + TABLE_INV +
                    " set stock = stock +" + stock + " where ean = " + '"' + ean + '"';
            db.execSQL(updateQuery);
        }
        db.close(); // Closing database connection

    }

    public void updateInv(String title, String ean, String supplier, String offer, String price, int stock){
        String updateQuery = "UPDATE " + TABLE_INV +
                " set title =" + title  +
                " set ean =" + ean + " set supplier =" + supplier +
                " set offer =" + offer +
                " set price =" + price +
                " set stock =" + stock +  " where ean = " + '"' + ean + '"';

    }


    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getAllInvDetails(String task) {
        HashMap<String, String> inv = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_INV +
                " where title = " + '"' + task + '"';

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()) {
            inv.put("title", cursor.getString(1));
            inv.put("ean", cursor.getString(2));
            inv.put("supplier", cursor.getString(3));
            inv.put("offer", cursor.getString(4));
            inv.put("stock", cursor.getString(5));
            inv.put("price", cursor.getString(6));
        }while (cursor.moveToNext());

        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + inv.toString());
        return inv;

    }

    public ArrayList<HashMap<String, String>> getSomeInvDetails() {
        ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_INV + " order by stock";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("title", cursor.getString(1));
                user.put("stock", cursor.getString(5));
                list.add(user);
            }while(cursor.moveToNext());}
        cursor.close();
        db.close();
        // return list
        Log.d(TAG, "Fetching list from Sqlite: " + list.toString());
        return list;
    }
    public ArrayList<HashMap<String, String>> getSomeInvReorderDetails() {
        ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_INV + " where stock <= reorder";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("title", cursor.getString(1));
                user.put("stock", cursor.getString(5));
                list.add(user);
            }while(cursor.moveToNext());}
        cursor.close();
        db.close();
        // return list
        Log.d(TAG, "Fetching list from Sqlite: " + list.toString());
        return list;
    }

    public String getProfit(){
      String profit = "0";
        String selectQuery = "SELECT  SUM(price) AS profit from" + TABLE_SALES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            profit = cursor.getString(cursor.getColumnIndex("profit"));
        }
        return profit;
    }

    public Integer getStock(String ean){
        int profit = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_INV +
        " where ean = " + '"' + ean + '"';

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            profit = cursor.getInt(cursor.getColumnIndex("stock"));
        }
        return profit;
    }

    public ArrayList<HashMap<String, String>> getSomeSaleDetails() {
        ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_SALES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("title", cursor.getString(1));
                user.put("stock", cursor.getString(2));
                list.add(user);
            }while(cursor.moveToNext());}
        cursor.close();
        db.close();
        // return list
        Log.d(TAG, "Fetching list from Sqlite: " + list.toString());
        return list;
    }

    public ArrayList<HashMap<String, String>> getSomeHistoryDetails(String ean) {
        ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
        Log.d(TAG, "passing ean to history list: " + ean.toString());
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY +
                " where ean = " + '"' + ean + '"';

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("offer", cursor.getString(4));
                user.put("price", cursor.getString(6));
                list.add(user);
            }while(cursor.moveToNext());}
        cursor.close();
        db.close();
        // return list
        Log.d(TAG, "Fetching history list from Sqlite: " + list.toString());
        return list;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount(String ean) {
        String countQuery = "SELECT  * FROM " + TABLE_INV + " where ean = " + '"' + ean + '"';
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Recreate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.delete(TABLE_INV, null, null);
        db.delete(TABLE_SALES, null, null);
        db.delete(TABLE_HISTORY, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}