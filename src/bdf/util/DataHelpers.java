package bdf.util;

import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.data.IBdfDatabase;

public class DataHelpers
{
	private static final char[] HEX = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	
	public static ByteBuffer getByteBuffer(IBdfDatabase db) {
		return ByteBuffer.wrap(db.getBytes());
	}
	
	public static BdfDatabase getDatabase(ByteBuffer buffer) {
		return new BdfDatabase(buffer.array());
	}
	
	public static byte[] serializeInt(int value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
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
	
	public static String replaceInString(String string, char find, String replace)
	{
		// Convert the string to bytes
		String string_modified = new String();
		
		// Loop over the string
		for(int i=0;i<string.length();i++)
		{
			// Is the byte to find the byte at this part of the string
			if(find == string.charAt(i))
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
	
	public static char[] replaceInCharArray(char[] chars, char find, String replace)
	{
		char[] replace_chars = replace.toCharArray();
		int replace_size = replace.length();
		
		int size = 0;
		
		// Get the new size of the char array
		for(int i=0;i<chars.length;i++) {
			if(chars[i] == find) {
				size += replace_size;
			} else {
				size += 1;
			}
		}
		
		char[] chars_modified = new char[size];
		int upto = 0;
		
		// Replace the contents of the char array
		for(int i=0;i<chars.length;i++)
		{
			if(chars[i] == find)
			{
				for(int j=0;j<replace_size;j++) {
					chars_modified[upto+j] = replace_chars[j];
				}
				
				upto += replace_size;
			}
			
			else
			{
				chars_modified[upto] = chars[i];
				upto += 1;
			}
		}
		
		return chars_modified;
	}
	
	public static String serializeString(String string)
	{
		char[] string_chars = string.toCharArray();
		
		// Replace some parts of the string
		string_chars = replaceInCharArray(string_chars, '\\', "\\\\");
		string_chars = replaceInCharArray(string_chars, '"', "\\\"");
		string_chars = replaceInCharArray(string_chars, '\n', "\\n");
		string_chars = replaceInCharArray(string_chars, '\t', "\\t");
		
		// Replace all the unreadable parts of the string
		{
			int size = 0;
			
			for(int i=0;i<string_chars.length;i++)
			{
				char c = string_chars[i];
				
				if(c < 0x20 || (c > 0x7e && c < 0xa1) || c == 0xad)
				{
					// Will be in the format \u0000
					size += 6;
				}
				
				else
				{
					size += 1;
				}
			}
			
			char[] chars = new char[size];
			int upto = 0;
			
			for(int i=0;i<string_chars.length;i++)
			{
				char c = string_chars[i];
				
				if(c < 0x20 || (c > 0x7e && c < 0xa1) || c == 0xad)
				{
					// Will be in the format \u0000
					chars[upto] = '\\';
					chars[upto+1] = 'u';
					chars[upto+2] = HEX[(c & 0xf000) >> 12];
					chars[upto+3] = HEX[(c & 0x0f00) >> 8];
					chars[upto+4] = HEX[(c & 0x00f0) >> 4];
					chars[upto+5] = HEX[(c & 0x000f)];
					upto += 6;
				}
				
				else
				{
					chars[upto] = string_chars[i];
					upto += 1;
				}
			}
			
			string_chars = chars;
		}
		
		// Add quotes to the string
		string = "\"" + new String(string_chars) + "\"";
		
		// Return the serialized string
		return string;
	}
}
