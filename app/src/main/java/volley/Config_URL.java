package volley;

//This class is for storing all URLs as a model of URLs

public class Config_URL
{
	private static String base_URL = "http://fyp1.000webhostapp.com/";		//Default configuration for WAMP - 80 is default port for WAMP and 10.0.2.2 is localhost IP in Android Emulator
	// Server user login url
	public static String URL_LOGIN = base_URL+"login.php";

	public static String URL_SEARCH = base_URL+"search.php";
	public static String URL_INV = base_URL+"inventory.php";
	public static String URL_UPDATE = base_URL+"update.php";
	public static String URL_SALE = base_URL+"sale.php";

	// Server user register url
	public static String URL_REGISTER = base_URL+"register.php";
/*

	*/
}
