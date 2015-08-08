import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// For every client's connection we call this class
public class clientThread extends Thread{
  private String username = null;

  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  
  private final clientThread[] threads;
  private int maxClientsCount;
  
  DBConnector dbtools = null;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
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
      
      dbtools = new DBConnector();
      
     
      
      while(true){
    	  os.println("+CONNECTION SUCCESSFULLY (HELP - available operations)");
    	  @SuppressWarnings("deprecation")
		String action = is.readLine();
    	  if(action.equals("EXIT")){
    		  is.close();
    	      os.close();
    	      clientSocket.close();
    	  }
    	  
    	  if(action.equals("HELP")){
    		  os.println("EXIT - to close connection");
        	  os.println("AUTH - to authorizate (get JSON object with \"username\" and \"password\" propierties)");
        	  os.println("REG  - to register (get JSON object with \"username\" , \"password\" and \"avatar\" propierties)");
        	  
          }
    	  if(action.equals("AUTH")){
        	  

            	  try{
        	    	  os.println("INPUT AUTH DATA:");
        	          @SuppressWarnings("deprecation")
					JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
        	          String inputUser = (String) inputJSON.get("username");
        	          String inputPass = (String) inputJSON.get("password");
        	          
        	          
        	          
        	    	  if(dbtools.getPasswordByUsername(inputUser).equals(inputPass)){
        	    		  this.username = inputUser;
        	    		  System.out.println(username + " logged successfully");
        	              os.println("+AUTH SUCCESS");
        	              break;
        	    		  
        	    	  }else{
        	    		  os.println("-AUTH FAILED");
        	    	  }
        	    	  
            	  }catch(Exception e){
            		  os.println("-AUTH FAILED");
            	  }
              
              
          }
    	  if(action.equals("REG")){
        	  dbtools = new DBConnector();

            	  try{
        	    	  os.println("INPUT AUTH DATA:");
        	          @SuppressWarnings("deprecation")
					JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
        	          
        	    	  if(dbtools.createNewClient(inputJSON)){
        	    		  System.out.println(username + " registrated successfully");
        	              os.println("+REGISTRATION SUCCESS");
        	    		  break;
        	    	  }else{
        	    		  os.println("-REGISTRATION FAILED");
        	    	  }
            	  }catch(Exception e){
            		  os.println("-REGISTRATION FAILED");
            	  }
              }
              
          
      }
      
      os.println("Welcome " + username);
      while(true){
    	  @SuppressWarnings("deprecation")
		String action = is.readLine();
    	  if(action.equals("EXIT")){
    		  break;
    	  }
    	  if(action.equals("HELP")){
    		  os.println("GETMSG  - get JSON Object with users and LinkedList Array message list");
    		  os.println("SENDMSG - send new messages (load JSON Object with \"to\" and \"text\" propierties)");
    		  os.println("USERS   - get JSON Array of all clients");
    	  }
    	  
    	  if(action.equals("GETMSG")){
    		  try{
    			  System.out.println("Send messages...");
    	    	  os.println(dbtools.getMessagesFor(username).toJSONString());
    	      }catch(NullPointerException e){
    	    	  os.println("-NO MESSAGES");
    	    	  System.out.println("no mesages");
    	      }
    	  }
    	  if(action.equals("SENDMSG")){
    		  while(true){
            	  try{
        	    	  os.println("INPUT SEND DATA:");
        	          @SuppressWarnings("deprecation")
					JSONObject inputJSON  = (JSONObject) new JSONParser().parse(is.readLine());
        	          String to = (String) inputJSON.get("to");
        	          String msgText = (String) inputJSON.get("text");
        	          
        	          if(!to.equals(null) || !msgText.equals(null)){
        	        	  dbtools.sendMsg(username, to, msgText);
            	          break;
        	          }else{
        	        	  
        	          }      	          

            	  }catch(Exception e){
            		  os.println("-SENDING FAILED");
            	  }
              }
              os.println("+SENDING SUCCESS");
    		  
    	  }
    	  
    	  if(action.equals("USERS")){
    		  try{
    			  
    	    	  os.println(dbtools.getAllClients().toJSONString());
    	      }catch(NullPointerException e){
    	    	  os.println("-NO CLIENTS");
    	    	  System.out.println("no clients");
    	      }
    	  }
    	  
      }
      
      

      //os.println(json);
      
      /*
      String name;
      while (true) {
        os.println("Enter your name.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          os.println("The name should not contain '@' character.");
        }
      }
      os.println("Welcome " + name);
      
     
      // Welcome the new the client.
      os.println("Welcome " + name
          + " to our chat room.\nTo leave enter /quit in a new line.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = "@" + name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].os.println("*** A new user " + name
                + " entered the chat room !!! ***");
          }
        }
      }
      // Start the conversation.
      while (true) {
        String line = is.readLine();
        if (line.startsWith("/quit")) {
          break;
        }
		
        // If the message is private sent it to the given client.
        if (line.startsWith("@")) {
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[0])) {
                    threads[i].os.println("<" + name + "> " + words[1]);
                    
                     //Echo this message to let the client know the private
                     //message was sent.
                     
                    this.os.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        } else {
			//additional command (see all users)
			if (line.startsWith("/users")) {
			  synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
				  if (threads[i] != null && threads[i].clientName != null) {
					this.os.println(threads[i].clientName + ";");
				  }
				}
			  }
			}
			else{
				// The message is public, broadcast it to all other clients.
				synchronized (this) {
					for (int i = 0; i < maxClientsCount; i++) {
					  if (threads[i] != null && threads[i].clientName != null) {
						threads[i].os.println("<" + name + "> " + line);
					  }
					}
				}
			}
          
        }
      }
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].os.println("*** The user " + name
                + " is leaving the chat room !!! ***");
          }
        }
      }
      os.println("*** Bye " + name + " ***");
      */
      
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
      
      
    } catch (Exception e) {
    	System.out.println(e);
    }
  }
}
