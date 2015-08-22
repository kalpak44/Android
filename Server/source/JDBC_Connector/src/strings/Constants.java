package strings;

public class Constants {
	public static final String CONNECTION_SUCCESSFULL = "+CONNECTION SUCCESSFULLY (HELP - available operations)";
	public static final String AHELP = "EXIT - to close connection\n"
			+ "AUTH - to authorizate (get JSON object with \"username\" and \"password\" propierties)\n"
			+ "REG  - to register (get JSON object with \"username\" , \"password\" and \"avatar\" propierties)";
	public static final String IData     = "INPUT AUTH DATA:";
	public static final String AFail     = "-AUTH FAILED";
	public static final String ASuccess  = "+AUTH SUCCESS";
	public static final String RFail     = "-REGISTRATION FAILED";
	public static final String RSuccess  = "+REGISTRATION SUCCESS";
	public static final String SEND_S    = "+SENDING SUCCESS";
	public static final String SEND_F    = "-SENDING FAILED";
	public static final String NOT_MSG   = "-NO MESSAGES";
	public static final String NO_USERS  = "-NO USERS";
	public static final String USERNAME  = "username";
	public static final String PASSWORD  = "password";
	public static final String TO        = "to";
	public static final String TEXT      = "text";
	public static final String UHELP  = ""
			+ "GETMSG     - load JSON Object with FROM key and return  JSONArray messages list\n"
			+ "SENDMSG    - send new messages (load JSON Object with \"to\" and \"text\" propierties)\n"
			+ "USERLIST   - get JSON Array of all clients\n"
			+ "LOGOUT     - return main menu";
	public static final String DB_URL  = "dburl";
	public static final String DB_USER = "dbuser";
	public static final String DB_PASS = "dbpassword";
	public static final String S_PORT  = "port";
	public static final String THREADS = "threads";
	public static final String FROM = "from";
	public static final String MESSAGE = "message";
	public static final String DATE_TIME = "date_time";
	
}
