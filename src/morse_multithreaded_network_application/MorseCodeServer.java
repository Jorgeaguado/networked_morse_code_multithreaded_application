package morse_multithreaded_network_application;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
 
public class MorseCodeServer 
{

   	private Client[] clients; // array of Clients
   	private ServerSocket server; // server socket to connect with clients
   	private ExecutorService runClients; // will run clients
   	private Lock clientLock; // to lock for synchronization
   	private Condition otherClientConnected; // to wait for other client

	private WriteLog log;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	//private Date date = new Date();
	private Calendar cal = Calendar.getInstance();


   	// set up  server 
   	public MorseCodeServer()
   	{
      		// create ExecutorService with a thread for each Client
      		runClients = Executors.newFixedThreadPool( 2 );
      		clientLock = new ReentrantLock(); // create lock for game

      		// condition variable for both Clients being connected
      		otherClientConnected = clientLock.newCondition();
      		clients = new Client[ 2 ]; // create array of Clients
 
      		try
      		{
         		server = new ServerSocket( 12345, 2 ); // set up ServerSocket
      		} // end try
      		catch ( IOException ioException ) 
      		{
         		ioException.printStackTrace();
         		System.exit( 1 );
      		} // end catch

		log = new WriteLog("morsecode.log");

   	} // end MorseCodeServer constructor

   	// wait for two connections so game can be Clients
   	public void execute()
   	{
		System.out.println("Waiting for a client ...");
      		// wait for each client to connect
      		for ( int i = 0; i < clients.length; i++ ) 
      		{
         		try // wait for connection, create Clients, start runnable
         		{
            			clients[ i ] = new Client( server.accept(), i );
            			runClients.execute( clients[ i ] ); // execute Client runnable
         		} // end try
         		catch ( IOException ioException ) 
         		{
            			ioException.printStackTrace();
            			System.exit( 1 );
         		} // end catch
      		} // end for

      		clientLock.lock(); // lock game to signal Client X's thread

      		try
      		{
         		clients[ 0 ].setSuspended( false ); // resume second client
         		otherClientConnected.signal(); // wake up first client
      		} // end try
      		finally
      		{
         		clientLock.unlock(); // unlock game after signalling Client X
      		} // end finally
   	} // end method execute
   

	public void sendMessage(String message, int number,  String senderIP ) 
	{
		//Socket receiverSock = clients[(number+1)%2].getSocket();
		String receiverIP = clients[(number+1)%2].getIP();

		try
		{	if((message).equals("OK"))
			{
				clients[number].writeMessage(message);
				log.writeToLog("TIME: "+dateFormat.format(cal.getTime())+"	IP SENDER: "+senderIP+"	CLIENT: "+(number+1)+"	CONNECTION: "+message);

			}
			else
			{
				clients[((number+1)%2)].writeMessage(message);
				log.writeToLog("TIME: "+dateFormat.format(cal.getTime())+"	IP SENDER: "+senderIP+"	IP RECEIVER: "+receiverIP+"	CLIENT WRITING: "+(number+1)+"	MESSAGE: "+message);
			}

        	}catch ( IOException ioException ) 
         	{
            		ioException.printStackTrace();
            		System.exit( 1 );
         	} // end c
	}

   	// private inner class Client manages each Client as a runnable
   	private class Client implements Runnable 
   	{
      		private Socket connection; // connection to client
      		private Scanner input; // input from client
      		private Formatter output; // output to client
      		private int clientNumber; // tracks which client this is
      		private boolean suspended = true; // whether thread is suspended
      		private String message = "";
      		private String ipClient;

      		// set up Client thread
      		public Client( Socket socket, int number )
      		{
         		clientNumber = number; // store this Client's number

         		connection = socket; // store socket for client

      		} // end Client constructor

		public void connect()
		{
         		try // obtain streams from Socket
         		{
				
            			input = new Scanner( connection.getInputStream() );
            			output = new Formatter( connection.getOutputStream() );
            			ipClient = input.nextLine();
            			sendMessage("OK", clientNumber, ipClient);

         		} // end try
         		catch ( IOException ioException ) 
         		{
            			ioException.printStackTrace();
            			System.exit( 1 );
         		} // end catch
		}

	      // control thread's execution
	      public void run()
	      {

			try 
			{
		            if ( clientNumber == 0 ) 
		            {
		            	System.out.println("The first client is connected.");
		                clientLock.lock(); // lock game to  wait for second Client		
               			try 
               			{
               					System.out.println("Waiting for another client ...");
                     			otherClientConnected.await(); // wait for Client O
               			} // end try 
               			catch ( InterruptedException exception ) 
               			{
               					exception.printStackTrace();
               			} // end catch
               			finally
               			{
               					clientLock.unlock(); // unlock game after second Client
               			} // end finally

            		} // end if
            		else
            		{
            			System.out.println("The second client is connected.");
            		} // end else

		            connect();

		            while(true)
		            {
		            	message = "";
		            	if ( input.hasNext() )
               			{
		            		message = input.nextLine();
		            		sendMessage(message, clientNumber,  ipClient);
               			}	
		            }
         	} // end try
         	finally
         	{
            		try
            		{
               			connection.close(); // close connection to client
            		} // end try
            		catch ( IOException ioException ) 
            		{
               			ioException.printStackTrace();
               			System.exit( 1 );
            		} // end catch
         	} // end finally

     	 } // end method run



	public void writeMessage(String messagetoSend)
	{
		output.format( "%s\n", messagetoSend ); 
	    output.flush(); // flush output
	}

	public String getIP()
	{
		return ipClient;
	}

      // set whether or not thread is suspended
      public void setSuspended( boolean status )
      {
         suspended = status; // set value of suspended
      } // end method setSuspended
   } // end class Clients
} // end class Server
