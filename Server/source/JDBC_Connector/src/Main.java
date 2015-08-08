import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import org.json.simple.JSONObject;




public class Main {
	// The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;

	  // This chat server can accept up to maxClientsCount clients' connections.
	  private static final int maxClientsCount = 100;
	  private static final clientThread[] threads = new clientThread[maxClientsCount];

	  
	  
	  
	  
	  
	public static void main(String[] args) {
		//generator auth json 
	      JSONObject gen  =new JSONObject();
	      gen.put("username","kalpak44");
	      gen.put("password","kalpak44");
	      System.out.println(gen.toJSONString());
	      
	      
//	      MessageBox mbox = new MessageBox();
//	      mbox.addMsg("vasq", "hello","14.06.2015 11:30");
//	      mbox.addMsg("kolq", "hello","14.06.2015 11:30");
//	      mbox.addMsg("vasq", "hoho", "14.06.2015 11:30");;
//	      
//	      System.out.println(mbox.toJsonObject());
	      
	      
		
		// The default port number.
	    int portNumber = 2222;
	    if (args.length < 1) {
	      System.out.println("Usage: java ChatServer <portNumber>\n"
	          + "Now using port number=" + portNumber);
	    } else {
	      portNumber = Integer.valueOf(args[0]).intValue();
	    }

	    /*
	     * Open a server socket on the portNumber (default 2222). Note that we can
	     * not choose a port less than 1023 if we are not privileged users (root).
	     */
	    try {
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }

	    
	    
	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	        clientSocket = serverSocket.accept();
	        int i = 0;
	        for (i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == null) {
	            (threads[i] = new clientThread(clientSocket, threads)).start();
	            break;
	          }
	        }
	        if (i == maxClientsCount) {
	          PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("Server too busy. Try later.");
	          os.close();
	          clientSocket.close();
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }



	}

}
