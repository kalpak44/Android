import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import strings.Constants;
import utils.DBTool;

	// For every client's connection we call this class
	public class clientThread extends Thread{
	private String username = null;
	
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	
	private final clientThread[] threads;
	private int maxClientsCount;
	private DBTool dbtools = null;
	
	  //Constructor
	public clientThread(Socket clientSocket, clientThread[] threads, DBTool dbtool) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		this.dbtools = dbtool;
		maxClientsCount = threads.length;
	}
	  

	
	
	public enum AuthTypes {
	      AUTH, REG, EXIT, HELP;
	}
		  
	  
	  
	  
	  
	private void authMenu() {
		try {
		os.println(Constants.CONNECTION_SUCCESSFULL);
		    switch( AuthTypes.valueOf(is.readLine().toUpperCase())) {
			    case EXIT:
			        System.out.println( "exit" );
			        is.close();
		  	      	os.close();
		  	      	clientSocket.close();
			        break;
			    case AUTH:
			        System.out.println( "auth" );
			        os.println(Constants.IData);
	
			        try{
			        	JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
			        	String inputUser = (String) inputJSON.get(Constants.USERNAME);
			        	String inputPass = (String) inputJSON.get(Constants.PASSWORD);
			        		  	          
			        	if(dbtools.getPass(inputUser).equals(inputPass)){
			        		this.username = inputUser;
			        		System.out.println(username + " logged");
			        		os.println(Constants.ASuccess);
			        		userMenu();
			        	}else{
			        		os.println(Constants.AFail);
			        		authMenu();
			        	}
			        }catch(Exception e){
			        	os.println(Constants.AFail);
			        	authMenu();
			        };
	
			        break;
			    case REG:
			        System.out.println( "reg" );
			        try{
	      	    	  os.println(Constants.IData);
	      	          @SuppressWarnings("deprecation")
						JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
	      	          
	      	    	  if(dbtools.createNewClient(inputJSON)){
	      	    		  System.out.println(username + " registrated");
	      	              os.println(Constants.RSuccess);
	      	              userMenu();
	      	    	  }else{
	      	    		  os.println(Constants.RFail);
	      	    		  authMenu();
	      	    		authMenu();
	      	    	  }
	          	  }catch(Exception e){
	          		  os.println(Constants.AFail);
	          		  authMenu();
	          	  }
			        break;
			    case HELP:
			        System.out.println( "help" );
			        os.println(Constants.AHELP);
			        authMenu();
			        break;
		    }
		}catch(Exception e){
			authMenu();
		}
	}
	
	public enum UserMenuTypes1 {
	      GETMSG, SENDMSG, USERLIST, LOGOUT, EXIT, HELP;
	}
	
	public void userMenu(){
		try {
			os.println("Welcome " + username);
		    switch( UserMenuTypes1.valueOf(is.readLine().toUpperCase())) {
			    case EXIT:
			        System.out.println( "exit" );
			        is.close();
		  	      	os.close();
		  	      	clientSocket.close();
			        break;
			    case GETMSG:
			    	try{
		    			  System.out.println("getmsg");
		    	    	  os.println(dbtools.getMessagesFor(username).toJSONString());
		    	    	  userMenu();
		    	     }catch(Exception e){
		    	    	  os.println(Constants.NOT_MSG);
		    	    	  userMenu();
		    	     } 
			    break;
			    case SENDMSG:
			    	 try{
			    		 System.out.println("sendmsg");
	        	    	 os.println(Constants.IData);
	        	         @SuppressWarnings("deprecation")
						 JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
	        	         String to = (String) inputJSON.get(Constants.TO);
	        	         String msgText = (String) inputJSON.get(Constants.TEXT);
	        	          
	        	         if(!to.equals(null) || !msgText.equals(null)){
	        	        	 dbtools.sendMsg(username, to, msgText);
	        	        	 os.println(Constants.SEND_S);
	        	        	 userMenu();
	        	         }else{
	        	        	os.println(Constants.SEND_F);
	        	        	userMenu();
	        	         }      	          

	            	  }catch(Exception e){
	            		  os.println(Constants.SEND_F);
	            		  userMenu();
	            	  }
			        break;
			    case HELP:
			        System.out.println( "help" );
			        os.println(Constants.UHELP);
			        userMenu();
			        break;
			case LOGOUT:
				authMenu();
				break;
			case USERLIST:
				System.out.println("userlist");
				try{
	    			 os.println(dbtools.getAllClientsWithCoundMsg(username).toJSONString());
	    			 userMenu();
	    	     }catch(NullPointerException e){
	    	    	 os.println(Constants.NO_USERS);
	    	    	 userMenu();
	    	    	 
	    	     }
				break;
		    }
			}catch(Exception e){
				userMenu();
			}
	}

  
  
  
  
  
  
  
  

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      
      
      authMenu();
      
       //Clean up. Set the current thread variable to null so that a new client
       //could be accepted by the server.
       
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
     
      //Close the output stream, close the input stream, close the socket.
       
      is.close();
      os.close();
      clientSocket.close();
      
      
    } catch (Exception e) {}
  }
  
  

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
