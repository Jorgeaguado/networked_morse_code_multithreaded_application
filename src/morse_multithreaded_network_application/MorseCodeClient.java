package morse_multithreaded_network_application;

import java.awt.BorderLayout;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;





public class MorseCodeClient extends JFrame implements Runnable 
{
	private JTextArea displayArea; // JTextArea to display output
   	private Socket connection; // connection to server
   	private Scanner input; // input from server
   	private Formatter output; // output to server
   	private String morseMessage = "";
	private String chatServer = "";
	private String ipLocal = "";
   	private JTextField enterField; // enters information from user

   	private static HashMap<String, String> codes = new HashMap<String, String>();
   	private static HashMap<String, String> decodes = new HashMap<String, String>();
    	static{

        	codes.put("A", ".-");		decodes.put(".-", "A");
        	codes.put("B", "-...");		decodes.put("-...", "B");
        	codes.put("C", "-.-.");		decodes.put("-.-.", "C");
        	codes.put("D", "-..");		decodes.put("-..", "D");
        	codes.put("E", ".");		decodes.put(".", "E");
        	codes.put("F", "..-.");		decodes.put("..-.", "F");
        	codes.put("G", "--.");		decodes.put("--.", "G");
        	codes.put("H", "....");		decodes.put("....", "H");
        	codes.put("I", "..");		decodes.put("..", "I");
        	codes.put("J", ".---");		decodes.put(".---", "J");
        	codes.put("K", "-.-");		decodes.put("-.-", "K");
        	codes.put("L", ".-..");		decodes.put(".-..", "L");
        	codes.put("M", "--");		decodes.put("--", "M");
        	codes.put("N", "-.");		decodes.put("-.", "N");
        	codes.put("O", "---");		decodes.put("---", "O");
        	codes.put("P", ".--.");		decodes.put(".--.", "P");
        	codes.put("Q", "--.-");		decodes.put("--.-", "Q");
        	codes.put("R", ".-.");		decodes.put(".-.", "R");
        	codes.put("S", "...");		decodes.put("...", "S");
        	codes.put("T", "-");		decodes.put("-", "T");
        	codes.put("U", "..-");		decodes.put("..-", "U");
        	codes.put("V", "...-");		decodes.put("...-", "V");
        	codes.put("W", ".--");		decodes.put(".--", "W");
        	codes.put("X", "-..-");		decodes.put("-..-", "X");
        	codes.put("Y", "-.--");		decodes.put("-.--", "Y");
        	codes.put("Z", "--..");		decodes.put("--..", "Z");
        	codes.put("1", ".----");	decodes.put(".----", "1");
        	codes.put("2", "..---");	decodes.put("..---", "2");
        	codes.put("3", "...--");	decodes.put("...--", "3");
        	codes.put("4", "....-");	decodes.put("....-", "4");
        	codes.put("5", ".....");	decodes.put(".....", "5");
        	codes.put("6", "-....");	decodes.put("-....", "6");
        	codes.put("7", "--...");	decodes.put("--...", "7");
        	codes.put("8", "---..");	decodes.put("---..", "8");
        	codes.put("9", "----.");	decodes.put("----.", "9");
        	codes.put("0", "-----");	decodes.put("-----", "0");
    	}



   	// set up user-interface and board
   	public MorseCodeClient( String ipLocal, String ipServer )
   	{ 
      		super( "Client" );

      		enterField = new JTextField(); // create enterField
      		enterField.setEditable( false );

      		enterField.addActionListener(
         		new ActionListener() 
         		{
            			// send message to server
            			public void actionPerformed( ActionEvent event )
            			{
               				sendData( event.getActionCommand() );
               				enterField.setText( "" );
            			} // end method actionPerformed
         		} // end anonymous inner class
      		); // end call to addActionListener

      		add( enterField, BorderLayout.NORTH );
      		displayArea = new JTextArea(); // create displayArea
		displayArea.setEditable(false);
      		add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      		setSize( 400, 350 ); // set size of window
      		setVisible( true ); // show window

		chatServer = ipServer;
		this.ipLocal = ipLocal;
      		startClient();

   	} // end MorseCodeClient constructor

	public void sendData( String message )
	{
         	output.format( "%s\n", convertToMorse(message) ); // send location to server
         	output.flush();
	} // end sendData

   	// start the client thread
   	public void startClient()
   	{
      		try // connect to server, get streams and start outputThread
      		{
         		// make connection to server
         		connection = new Socket( InetAddress.getByName( chatServer ) , 12345 );
         		// get streams for input and output
         		input = new Scanner( connection.getInputStream() );
         		output = new Formatter( connection.getOutputStream() );
      		} // end try
      		catch ( IOException ioException )
      		{
         		ioException.printStackTrace();         
      		} // end catch

	      // create and start worker thread for this client
	      ExecutorService worker = Executors.newFixedThreadPool( 1 );
	      worker.execute( this ); // execute client
	} // end method startClient

	// control thread that allows continuous update of displayArea
   	public void run()
   	{
		String aux = "";
		do
		{
			output.format( "%s\n", this.ipLocal );
         		output.flush();

			if(input.hasNextLine())
				aux = input.nextLine();
		}while(!aux.equals("OK"));

      		enterField.setEditable( true );

      		while ( true )
      		{
			if ( input.hasNextLine() )
			{
				String translation = "";
				String receivedMessage = input.nextLine();
				String[] messageSplited = receivedMessage.split("   ");

				for(String str: messageSplited)
				{
					translation = translation + translateMorse(str);
					translation = translation + " ";
				}

            		displayMessage(receivedMessage, translation );

			} // end if
      		} // end while
   	} // end method run


	public String convertToMorse(String auxmessage)
	{
		morseMessage = "";
		for(int i = 0; i < auxmessage.length(); i++)
		{
			char myChar = auxmessage.charAt(i);
			
			if(auxmessage.charAt(i)!=' ')
			{
				if((codes.containsKey(String.valueOf(auxmessage.charAt(i)).toUpperCase())))
				{
					morseMessage = morseMessage + codes.get(String.valueOf(auxmessage.charAt(i)).toUpperCase());
					morseMessage = morseMessage + " ";
				}
			}
			else if(auxmessage.charAt(i)==' ')
				morseMessage = morseMessage + "   ";
			
		} 
		
		return morseMessage;
	}// end convertToMorse

	

	public String translateMorse(String auxmessage)
	{
		morseMessage = "";
		String[] auxm = auxmessage.split(" ");

		for (String str : auxm)
		{
			if((!str.equals("")) && (!str.equals(" ")))
			{
    				morseMessage = morseMessage + decodes.get(str);
			}
		}
		
		return morseMessage;
	}// end translateMorse


   	// manipulate outputArea in event-dispatch thread
   	private void displayMessage( final String messageReceived, final String translation )
   	{
      		SwingUtilities.invokeLater(
         		new Runnable() 
         		{
            			public void run() 
            			{
               				displayArea.append( "Morse code:\n"+messageReceived ); // updates output
					displayArea.append( "\n" ); // updates output
               				displayArea.append( "Normal text:\n"+translation ); // updates output
					displayArea.append( "\n" ); // updates output
		
            			} // end method run
         		}  // end inner class
      		); // end call to SwingUtilities.invokeLater
   	} // end method displayMessage


} // end class MorseCodeClient

