package bdf.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class FileHelpers
{
	public static byte[] readAll(InputStream in)
	{
		try
		{
			// Get bytes to return
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int size = 0;
			
			while((size = in.read(bytes)) != -1) {
				buffer.write(bytes, 0, size);
			}
			
			// Send the bytes collected from the file stream back
			return buffer.toByteArray();
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
	
	public static byte[] readAllCompressed(String path) throws IOException
	{
		// Create the file input stream
		InflaterInputStream in = new InflaterInputStream(new FileInputStream(path));
		
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
	
	public static byte[] readAllCompressedIgnoreErrors(String path)
	{
		try {
			return readAllCompressed(path);
		}
		
		catch(IOException | RuntimeException e) {
			return new byte[0];
		}
	}
}
