import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import org.json.simple.JSONObject;

import strings.Config;






public class Main {
	// The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;
	  
	  private static final clientThread[] threads = new clientThread[Config.maxClientsCount];

	  

	  
	  
	  
	  
	  
	public static void main(String[] args) {
		
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
	        for (i = 0; i < Config.maxClientsCount; i++) {
	          if (threads[i] == null) {
	            (threads[i] = new clientThread(clientSocket, threads)).start();
	            break;
	          }
	        }
	        if (i == Config.maxClientsCount) {
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
