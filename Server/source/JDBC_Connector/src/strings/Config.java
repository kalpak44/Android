package strings;

public class Config {
		// JDBC driver name and database URL
		public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
		public static final String DEFAULT_JDB_URL = "localhost:3306/mychat";
		
		//  Database credentials
		public static final String DEFAULT_DB_USER = "root";
		public static final String DEFAULT_DB_PASS = "root";
		
		
		// This chat server can accept up to maxClientsCount clients' connections.
		 public static final int defaultMaxClientsCount = 100;
		 
		// The default port number.
		 public static final int defaultPortNumber = 2222;
		 
		 public static final String CONFIG_FILE = "config.properties"; 
		 public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
		 
	
}
