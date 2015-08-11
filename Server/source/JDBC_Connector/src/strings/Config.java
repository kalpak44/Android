package strings;

public class Config {
		// JDBC driver name and database URL
		public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
		public static final String DB_URL = "jdbc:mysql://localhost:3306/mychat";
		
		//  Database credentials
		public static final String USER = "root";
		public static final String PASS = "root";
		
		
		// This chat server can accept up to maxClientsCount clients' connections.
		 public static final int maxClientsCount = 100;
		 
	
}
