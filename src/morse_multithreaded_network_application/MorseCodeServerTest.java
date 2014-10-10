package morse_multithreaded_network_application;

import java.io.IOException;

public class MorseCodeServerTest
{
   public static void main( String args[] ) throws IOException
   {
      MorseCodeServer application = new MorseCodeServer();
      application.execute();
   } // end main
} // end class MorseCodeServerTest