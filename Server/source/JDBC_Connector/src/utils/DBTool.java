package utils;
import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import strings.Config;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DBTool {
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	public DBTool(){
		try{
			// Register JDBC driver
			Class.forName(Config.JDBC_DRIVER);
			conn  = DriverManager.getConnection(Config.DB_URL,Config.USER,Config.PASS);			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	
	
	// Get password by username method
	
	public String getPass(String username){
		try{
			// Execute a query

			stmt = conn.createStatement();			
			String sql = "select password from clients where username = ?";
			
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getString("password");

		}catch(Exception ex){
			System.out.println("exept get pass by username method");
		}
		return null;
		      
	}
	
	
	
	
	
	
	public boolean  createNewClient(JSONObject client){
		try{
			stmt = conn.createStatement();
			String username = (String) client.get("username");
			String password = (String) client.get("password");
			String avatar   = (String) client.get("avatar");
			
			if(this.getPass(username)!=null){
				return false;
			}

			String sql = "INSERT INTO clients"
					+ "(username, password, avatar) VALUES"
					+ "(?,?,?)";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, avatar);
			
			preparedStatement.executeUpdate();
			
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
			stmt = conn.createStatement();
			String sql = "select `id`,`from`,`message`, `date_time` from messages where is_readed = 0 and `to` = ?";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);

			rs = preparedStatement.executeQuery();
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
			stmt = conn.createStatement();
			
			
			String sql = "select `username` from clients";
			String sql1 = "select clients.username, count(messages.id) as messages"
					+ " from clients left join messages on clients.username = messages.to"
					+ " where messages.is_readed = 0 group by username";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql1);
			
			
			rs = preparedStatement.executeQuery();
			
			
			result = new JSONArray();
			while(rs.next()){
				String username = rs.getString("username");
				int messages  = rs.getInt("messages");
				JSONObject json = new JSONObject();
				json.put(username, messages);
				result.add(json);
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
			System.out.println(msgText);

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
