package tests;

import java.io.IOException;

import bdf.data.IBdfDatabase;
import bdf.types.BdfIndent;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;
import bdf.types.BdfReader;

public class Tests {

	public static void main(String[] args) throws InterruptedException, IOException
	{
		BdfReader reader = new BdfReader();
		BdfObject bdf = reader.getBDF();
		BdfNamedList nl = bdf.getNamedList();
		nl.set("hello", nl.createObject().setInteger(69));
		nl.set("world", nl.createObject().setString("👋"));
		nl.set("👋", nl.createObject().setArray(nl.createArray()));
		
		reader.serializeHumanReadable(System.out);
		System.out.println();
		
		IBdfDatabase db = reader.serialize();
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
		
		reader = new BdfReader(db);
		reader.serializeHumanReadable(System.out);
		System.out.println();
	}

}
