package morse_multithreaded_network_application;

import java.io.IOException;
import java.util.HashMap;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.util.List;
import java.util.ArrayList;



public class ReadLogFile extends JFrame
{
	private JTextArea displayArea; // JTextArea to display output
   	private static HashMap<String, String> codes = new HashMap<String, String>();
   	private static HashMap<String, String> decodes = new HashMap<String, String>();
    	static{
        	decodes.put(" ", " ");
        	decodes.put(".-", "A");
        	decodes.put("-...", "B");
        	decodes.put("-.-.", "C");
        	decodes.put("-..", "D");
        	decodes.put(".", "E");
        	decodes.put("..-.", "F");
        	decodes.put("--.", "G");
        	decodes.put("....", "H");
        	decodes.put("..", "I");
        	decodes.put(".---", "J");
        	decodes.put("-.-", "K");
        	decodes.put(".-..", "L");
        	decodes.put("--", "M");
        	decodes.put("-.", "N");
        	decodes.put("---", "O");
        	decodes.put(".--.", "P");
        	decodes.put("--.-", "Q");
        	decodes.put(".-.", "R");
        	decodes.put("...", "S");
        	decodes.put("-", "T");
        	decodes.put("..-", "U");
        	decodes.put("...-", "V");
        	decodes.put(".--", "W");
        	decodes.put("-..-", "X");
        	decodes.put("-.--", "Y");
        	decodes.put("--..", "Z");
		decodes.put(".----", "1");
		decodes.put("..---", "2");
        	decodes.put("...--", "3");
        	decodes.put("....-", "4");
        	decodes.put(".....", "5");
        	decodes.put("-....", "6");
        	decodes.put("--...", "7");
        	decodes.put("---..", "8");
        	decodes.put("----.", "9");
        	decodes.put("-----", "0");
    	}

	public ReadLogFile()
	{
		super("Log File ");
		displayArea = new JTextArea(); // create displayArea
		displayArea.setEditable(false);
      		add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      		setSize( 400, 350 ); // set size of window
      		setVisible( true ); // show window

	}

	public String translateMorse(String auxmessage)
	{
		String word = "   ";
		String morseMessage = "";

		String[] auxWords = auxmessage.split(word);
		for(String strW : auxWords)
		{
			String[] auxm = strW.split(" ");

			for (String str : auxm)
			{
				if((!str.equals("")) && (!str.equals(" ")))
				{
    					morseMessage = morseMessage + decodes.get(str);
				}
			}
			
			morseMessage = morseMessage + " ";
		}
		
		return morseMessage;
	}// end translateMorse

	public void addLineToPanel(String line)
	{
		displayArea.append( line+"\n" ); // updates output
	}

	public String getMorseFromLine(String line)
	{
		String codeMessage = line.substring(line.indexOf("MESSAGE:")+9, line.length());

		return codeMessage;
	}

	public String getInformationFromLine(String line)
	{
		String inform = line.substring(0, line.indexOf("MESSAGE:"));

		return inform;
	}

	public static void main(String args[])
	{
		ReadLogFile prueba = new ReadLogFile();
      		prueba.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		WriteLog log = new WriteLog("morsecode.log");
		int counter = 0;
		List<String> listLine  = new ArrayList<String>();
		try
		{
			listLine = log.readFile();
		}
		catch ( IOException ioException ) 
         	{
            		ioException.printStackTrace();
            		System.exit( 1 );
         	} // end c
	
		for(Object object : listLine) {
    			String element = (String) object;
			if(element.contains("MESSAGE:" ))
			{
				String morseCode = prueba.getMorseFromLine(element);
				String inform = prueba.getInformationFromLine(element);
				String message = prueba.translateMorse(morseCode);
				prueba.addLineToPanel(inform+"MESSAGE: "+message);

			}
			else if(element.contains("CONNECTION:"))
				prueba.addLineToPanel(element);
			counter++;
		}
	}

}