import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Properties;
import java.net.ServerSocket;
import strings.Config;
import strings.Constants;
import strings.Strings;
import utils.DBTool;







public class Main {
	// The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;
	  
	  private static clientThread[] threads;
	  private static int sPort;
	  private static int threadsCount;
	  private static String dbUrl;
	  private static String dbUser;
	  private static String dbPass;
	  
	  private static DBTool dbtools;

	  
	  
	public static void main(String[] args) {
	    if (args.length < 1) {
	      System.out.println(Strings.HELLO);
	      
	      sPort = Config.defaultPortNumber;
	      threadsCount = Config.defaultMaxClientsCount;
	      dbUrl = Config.DEFAULT_JDB_URL;
	      dbUser= Config.DEFAULT_DB_USER;
	      dbPass= Config.DEFAULT_DB_PASS;
	    } else {
	      String arg0 = args[0].toString().toLowerCase();
	      if(arg0.equals("-config")){
    	  try{
    		  System.out.println(Strings.S_PORT);
    		  BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      String s = bufferRead.readLine();
    	      sPort = Integer.parseInt(s);
    	      System.out.println("Now using: "+sPort);
    	      
    	      System.out.println(Strings.THREADS);
    	      bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      s = bufferRead.readLine();
    	      threadsCount = Integer.parseInt(s);
    	      System.out.println(threadsCount+" - ok");
    	      
    	      System.out.println(Strings.DB_URL);
    	      bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      dbUrl = bufferRead.readLine();
    	      
    	      System.out.println(Strings.DB_USER);
    	      bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      dbUser = bufferRead.readLine();
    	      
    	      System.out.println(Strings.DB_PASS);
    	      bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      dbPass = bufferRead.readLine();
    	      
    	      System.out.println(Strings.SAVE_TO_FILE);
    	      bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	      s = bufferRead.readLine().toLowerCase();
    	      
    	      if(s.equals("y")){
    	    	  Properties prop = new Properties();
    	    		OutputStream output = null;

    	    		try {

    	    			output = new FileOutputStream(Config.CONFIG_FILE);

    	    			// set the properties value
    	    			prop.setProperty(Constants.DB_URL, dbUrl);
    	    			prop.setProperty(Constants.DB_USER, dbUser);
    	    			prop.setProperty(Constants.DB_PASS, dbPass);
    	    			prop.setProperty(Constants.S_PORT, ""+sPort);
    	    			prop.setProperty(Constants.THREADS, ""+threadsCount);
    	    			

    	    			// save properties to project root folder
    	    			prop.store(output, null);

    	    		} catch (IOException io) {
    	    			System.out.println(Strings.SAVE_EXCEPTION);
    	    		} finally {
    	    			if (output != null) {
    	    				try {
    	    					output.close();
    	    				} catch (IOException e) {
    	    					e.printStackTrace();
    	    				}
    	    			}

    	    		}
    	      }
    	      System.out.println(Strings.SAVE_SUCCESS);
    	    }
    	    catch(IOException e)
    	    {
    	    	System.out.println(Strings.INVALID_CONF);
    	    	System.exit(0);
    	    }
	      }if(arg0.equals("-l")){
	    	  Properties prop = new Properties();
	    		InputStream input = null;

	    		try {

	    			input = new FileInputStream(Config.CONFIG_FILE);

	    			// load a properties file
	    			prop.load(input);


	    			// get the property value
	    			dbUrl  = prop.getProperty(Constants.DB_URL);
	    			dbUser = prop.getProperty(Constants.DB_USER);
	    			dbPass = prop.getProperty(Constants.DB_PASS);
	    			sPort  = Integer.parseInt(prop.getProperty(Constants.S_PORT));
	    			threadsCount  = Integer.parseInt(prop.getProperty(Constants.THREADS));

	    		} catch (IOException ex) {
	    			System.out.println(Strings.LOAD_FILE_ERROR);
	    		} finally {
	    			if (input != null) {
	    				try {
	    					input.close();
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		}
	      }if(arg0.equals("-help")){
	    	  System.out.println(Strings.HELLO);
	      }
	      
	    }
	    
	    System.out.println(
	      		"Now server using port number=" +sPort+"\n"
	      		+ "This chat server can accept up to "+threadsCount+" clients' connections\n"
	      		+ "DB: "+dbUrl+"\n"
	      		+ "DB User:     "+dbUser+"\n"
	      		+ "DB Password: "+dbPass+"\n\n"
	      				+ "start server..."
	    );
	    threads = new clientThread[threadsCount];
	    
	    
	    

	    
	    //Server init
	    
	    try {
	      serverSocket = new ServerSocket(sPort);
	      dbtools = new DBTool("jdbc:mysql://"+dbUrl, dbUser, dbPass);
	    } catch (Exception e) {
	      System.out.println(Strings.SERVER_INIT_FAILED);
	      System.exit(0);
	    }
	    
	    
	    

	    
	    
	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	        clientSocket = serverSocket.accept();
	        int i = 0;
	        for (i = 0; i < threadsCount; i++) {
	          if (threads[i] == null) {
	            (threads[i] = new clientThread(clientSocket, threads, dbtools)).start();
	            break;
	          }
	        }
	        if (i == threadsCount) {
	          PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println(Strings.SERV_BUSY);
	          os.close();
	          clientSocket.close();
	        }
	      } catch (IOException e) {
	        System.out.println(e.getMessage());
	      }
	      
	      
	    }


	}

}
