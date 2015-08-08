import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Properties;




public class DBConnector {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	static final String DB_URL = "jdbc:mysql://localhost:3306/mychat";
	
	//  Database credentials
	static final String USER = "root";
	static final String PASS = "powermedoed13";
	
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	public DBConnector(){
	
		try{
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			
			System.out.println("Connecting to a selected database...");
			conn  = DriverManager.getConnection(DB_URL,USER,PASS);
			System.out.println("Connected database successfully...");
			
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	
	
	public void seeData(){
		try{
			// Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			
			String sql = "select * from clients";
			rs = stmt.executeQuery(sql);
			
			// Display values
			while(rs.next()){
				String username = rs.getString("username");
				System.out.println("username: "+username);
				
				String password = rs.getString("password");
				System.out.println("password: "+password);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		      
	}
	
	public String getPasswordByUsername(String username){
		String result = null;
		try{
			// Execute a query
			System.out.println("Creating statement (getPasswordByUsername)...");
			stmt = conn.createStatement();
			
			String sql = "select * from clients";
			rs = stmt.executeQuery(sql);
			
			// Display values
			while(rs.next()){
				String user = rs.getString("username");
				if(user.equals(username)){
					result = rs.getString("password");
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
		      
	}
	
	public boolean  createNewClient(JSONObject client){
		try{
			System.out.println("Creating statement (createNewClient)...");
			stmt = conn.createStatement();
			System.out.println("parse input json");
			String username = (String) client.get("username");
			String password = (String) client.get("password");
			String avatar   = (String) client.get("avatar");
			
			System.out.println("checking client in database...");
			if(this.getPasswordByUsername(username)!=null){
				return false;
			}
			System.out.println("Creating PreparedStatement...");
			String sql = "INSERT INTO clients"
					+ "(username, password, avatar) VALUES"
					+ "(?,?,?)";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, avatar);
			
			System.out.println("execute insert SQL stetement...");
			preparedStatement.executeUpdate();
			System.out.println("client was added.");
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	public JSONObject getMessagesFor(String username){
		JSONObject result = null;
		try{
			System.out.println("Creating statement (MessagesForUser)...");
			stmt = conn.createStatement();
			
			System.out.println("Creating PreparedStatement...");
			String sql = "select `id`,`from`,`message`, `date_time` from messages where is_readed = 0 and `to` = ?";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			
			System.out.println("execute select SQL stetement");
			rs = preparedStatement.executeQuery();
			System.out.println("get msg for: "+username);

			MessageBox msgbox = new MessageBox();

			while(rs.next()){
				int id = rs.getInt("id");
				String from = rs.getString("from");
				String message = rs.getString("message");
				String dateTime = rs.getString("date_time");
				
				msgbox.addMsg(from, message, dateTime);
				markMsgAsReaded(id);
				
			}
			result = msgbox.toJsonObject();
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	 
	private void markMsgAsReaded(int id){
		try
	    {
	      String query = "update messages set is_readed = ? where id = ?";
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt   (1, 1);
	      preparedStmt.setInt   (2, id);
	      preparedStmt.executeUpdate();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	    }
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getAllClients(){
		JSONArray result = new JSONArray();
		try{
			System.out.println("Creating statement (MessagesForUser)...");
			stmt = conn.createStatement();
			
			System.out.println("Creating PreparedStatement...");
			String sql = "select `username` from clients";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			
			System.out.println("execute select SQL stetement");
			rs = preparedStatement.executeQuery();
			
			
			result = new JSONArray();
			while(rs.next()){
				String username = rs.getString("username");
				result.add(username);
			}
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public boolean  sendMsg(String from, String to, String msgText){
		try{
			System.out.println("Creating statement (sendMsg)...");
			stmt = conn.createStatement();

			System.out.println("Creating PreparedStatement...");
			String sql = "insert into `messages` (`from`, `to`, `message`, `date_time`) values (?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			

			preparedStatement.setString(1, from);
			preparedStatement.setString(2, to);
			preparedStatement.setString(3, msgText);
			preparedStatement.setString(4, dateFormat.format(date));
			
			
			System.out.println("execute insert SQL stetement...");
			preparedStatement.executeUpdate();
			System.out.println("sended...");
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void close(){
		//finally block used to close resources
		try{
			if(stmt!=null)
				conn.close();
		}catch(SQLException se){}// do nothing
	    try{
	    	if(conn!=null)
	    		conn.close();
	    }catch(SQLException se){
	    	se.printStackTrace();
	    }//end try
	    System.out.println("Goodbye!");
	}

}
