package morse_multithreaded_network_application;

import javax.swing.JFrame;
import javax.swing.JApplet;
import java.net.*;

public class MorseCodeClientTest extends JApplet
{

	public static void main( String args[] )
	{
      		MorseCodeClient application; // declare client application
		InetAddress thisIp;
		try {
			thisIp = InetAddress.getLocalHost();
         		application = new MorseCodeClient( thisIp.getHostAddress() ,"127.0.0.1" ); //Local IP, Server IP
      			application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
     		}
    		catch(Exception e) {
     			e.printStackTrace();
     		}
	
   } // end main

} // end class MorseCodeClientTest

