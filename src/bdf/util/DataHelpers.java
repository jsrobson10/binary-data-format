package bdf.util;

import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.data.IBdfDatabase;

public class DataHelpers
{
	public static ByteBuffer getByteBuffer(IBdfDatabase db) {
		return ByteBuffer.wrap(db.getBytes());
	}
	
	public static BdfDatabase getDatabase(ByteBuffer buffer) {
		return new BdfDatabase(buffer.array());
	}
	
	public static byte[] serializeInt(int value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE/8);
		buffer.putInt(value);
		return buffer.array();
	}
	
	public static boolean bytesAreEqual(byte[] b1, byte[] b2)
	{
		// Send back false if the lengths are different
		if(b1.length != b2.length) return false;
		
		// Loop over the bytes
		for(int i=0;i<b1.length;i++)
		{
			// Send back false if the bytes are different
			if(b1[i] != b2[i]) return false;
		}
		
		// Send back true if everything has been checked
		return true;
	}
	
	public static String replaceInString(String string, byte find, String replace)
	{
		// Convert the string to bytes
		byte[] string_b = string.getBytes();
		String string_modified = new String();
		
		// Loop over the string
		for(int i=0;i<string_b.length;i++)
		{
			// Is the byte to find the byte at this part of the string
			if(find == string_b[i])
			{
				// Add the data to replace to the string
				string_modified += replace;
			}
			
			else
			{
				// Add the part of the old string to the new string
				string_modified += string.substring(i, i+1);
			}
		}
		
		// Send back the modified string
		return string_modified;
	}
	
	public static String replaceInString(String string, char find, String replace) {
		return replaceInString(string, (byte)find, replace);
	}
	
	public static String serializeString(String string)
	{
		// Serialize the string
		String serialized = string;
		
		// Replace some parts of the string
		serialized = replaceInString(serialized, '\\', "\\\\");
		serialized = replaceInString(serialized, '"', "\\\"");
		serialized = replaceInString(serialized, '\n', "\\n");
		serialized = replaceInString(serialized, '\t', "\\t");
		
		// Add quotes to the string
		serialized = "\"" + serialized + "\"";
		
		// Return the serialized string
		return serialized;
	}
}
