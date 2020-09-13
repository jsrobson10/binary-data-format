package tests;

import java.io.FileInputStream;
import java.io.IOException;

import bdf.data.IBdfDatabase;
import bdf.types.BdfReader;
import bdf.types.BdfReaderHuman;

public class Tests
{
	public static void displayHex(IBdfDatabase db)
	{
		char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		
		System.out.print("Size: " + db.size() + ", Hex: ");
		
		for(int i=0;i<db.size();i++)
		{
			int b = db.getByte(i);
			if(b < 0) b += 128;
			
			System.out.print(hex_chars[b / 16]);
			System.out.print(hex_chars[b % 16]);
			System.out.print(' ');
		}
		
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException
	{
		new BdfReaderHuman("int(NaND)");
	}

}
