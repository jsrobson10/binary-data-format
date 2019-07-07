package bdf.util;

import java.io.FileInputStream;
import java.io.IOException;

public class FileHelpers
{
	public static byte[] readAll(FileInputStream in)
	{
		try
		{
			// Get bytes to return
			int available = in.available();
			byte[] bytes = new byte[available];
			
			// Loop over the available bytes
			for(int i=0;i<available;i++)
			{
				// Add the next byte from the stream to the bytes
				bytes[i] = (byte) in.read();
			}
			
			// Send the bytes collected from the file stream back
			return bytes;
		}
		
		catch (IOException e)
		{
			// Throw the IOException as a runtime exception
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] readAll(String path) throws IOException
	{
		// Create the file input stream
		FileInputStream in = new FileInputStream(path);
		
		// Load all of its data
		byte[] data = readAll(in);
		
		// Close the file input stream
		in.close();
		
		// Send back the data
		return data;
	}
	
	public static byte[] readAllIgnoreErrors(String path)
	{
		try {
			return readAll(path);
		}
		
		catch(IOException e) {
			return new byte[0];
		}
	}
}
