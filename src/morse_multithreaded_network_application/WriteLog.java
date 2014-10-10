package morse_multithreaded_network_application;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

public class WriteLog
{
	private String path;
	
	public WriteLog(String file_path)
	{
		path = file_path;
	}

	public void writeToLog(String textLine) throws IOException
	{

		FileWriter writer = new FileWriter (path, true);
		PrintWriter print_line = new PrintWriter(writer);

		print_line.printf("%s"+" %n", textLine);
		print_line.close();
	}
	
	public List<String> readFile() throws IOException
	{
		FileReader file = new FileReader(path);
		BufferedReader bf =  new BufferedReader(file);
		String line;
		List<String> listLine  = new ArrayList<String>();

		while((line = bf.readLine()) != null)
			listLine.add(line);

		bf.close();
		return listLine;
	}

}