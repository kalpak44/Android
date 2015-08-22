package utils;
import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import strings.Config;
import strings.Constants;
import strings.Strings;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DBTool {
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	public DBTool(String dbUrl,String dbUser,String dbPassword) throws ClassNotFoundException, SQLException{
			// Register JDBC driver
		Class.forName(Config.JDBC_DRIVER);
		conn  = DriverManager.getConnection(dbUrl,dbUser,dbPassword);			

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
			return rs.getString(Constants.PASSWORD);

		}catch(Exception ex){
			System.out.println("exept get pass by username method");
		}
		return null;
		      
	}
	
	
	
	
	
	
	public boolean  createNewClient(JSONObject client){
		try{
			stmt = conn.createStatement();
			String username = (String) client.get(Constants.USERNAME);
			String password = (String) client.get(Constants.PASSWORD);
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
	
	@SuppressWarnings("unchecked")
	public JSONArray getMessages(String from, String to){
		JSONArray result = null;
		try{
			stmt = conn.createStatement();
			String sql = "select `id`,`from`,`message`, `date_time` from messages where is_readed = 0 and `to` = ? and `from` = ?";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, to);
			preparedStatement.setString(2, from);
			
			rs = preparedStatement.executeQuery();
			
			result = new JSONArray();
			while(rs.next()){
				result.add(rs.getString(Constants.MESSAGE));
				markMsgAsReaded(rs.getInt("id"));
			}

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
	public JSONArray getAllClientsWithCoundMsg(String username){
		JSONArray result = new JSONArray();
		try{
			stmt = conn.createStatement();
			
			
			String sql1 = "select clients.username, count(messages.id) as messages from clients left join messages on clients.username = messages.from and messages.to = ? and messages.is_readed = 0 group by username;";
			PreparedStatement preparedStmt = conn.prepareStatement(sql1);
			preparedStmt = conn.prepareStatement(sql1);
			preparedStmt.setString(1, username);
			
			
			rs = preparedStmt.executeQuery();
			
			
			result = new JSONArray();
			while(rs.next()){
				JSONObject json = new JSONObject();
				String currUsersrname = rs.getString(Constants.USERNAME);
				int currMsgCount = rs.getInt("messages");
				if(!currUsersrname.equals("")&&!currUsersrname.equals(username)){
					json.put(currUsersrname, currMsgCount);
					result.add(json);
				}
				
				
			}
			
			if (preparedStmt != null) {
				preparedStmt.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public boolean  sendMsg(String from, String to, String msgText){
		try{

			stmt = conn.createStatement();
			System.out.println(msgText);

			String sql = "insert into `messages` (`from`, `to`, `message`, `date_time`) values (?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			
			DateFormat dateFormat = new SimpleDateFormat(Config.DATE_TIME_FORMAT);
			Date date = new Date();
			

			preparedStatement.setString(1, from);
			preparedStatement.setString(2, to);
			preparedStatement.setString(3, msgText);
			preparedStatement.setString(4, dateFormat.format(date));
			
			
			preparedStatement.executeUpdate();
			System.out.println(Strings.MSG_SENDED);
			
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
	    	if(conn!=null)
	    		conn.close();
	    }catch(Exception se){
	    }//end try
	    System.out.println(Strings.BYE);
	}

}
