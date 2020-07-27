package tests;

import java.io.IOException;

import bdf.data.IBdfDatabase;
import bdf.types.BdfIndent;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;
import bdf.types.BdfReader;

public class Tests
{
	static void displayHex(IBdfDatabase db)
	{
		char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		
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
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		BdfReader reader = new BdfReader();
		BdfObject bdf = reader.getObject();
		
		BdfNamedList nl = bdf.newNamedList();
		bdf.setNamedList(nl);
		
		nl.set("Hello, ", bdf.newObject().setInteger(69));
		nl.set("world!", bdf.newObject().setInteger(420));
		nl.remove("Hello, ");
		
		reader.serializeHumanReadable(System.out);
		
		IBdfDatabase db = reader.serialize();
		displayHex(db);
		
		reader = new BdfReader(db);
		
		reader.serializeHumanReadable(System.out);
		
		reader.getObject().setArray(bdf.newArray());
		
		db = reader.serialize();
		displayHex(db);
		
		reader.serializeHumanReadable(System.out);
	}

}
