package tests;

import java.io.FileOutputStream;
import java.io.IOException;

import bdf.classes.IBdfClassManager;
import bdf.data.IBdfDatabase;
import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfIndent;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;
import bdf.types.BdfReader;
import bdf.types.BdfTypes;
import bdf.util.FileHelpers;

public class Tests
{
	private static class Storage implements IBdfClassManager
	{
		String name;
		int type;
		int age;
		
		public Storage(String name, int age, int type) {
			this.name = name;
			this.age = age;
			this.type = type;
		}

		@Override
		public void BdfClassLoad(BdfObject bdf)
		{
			
		}

		@Override
		public void BdfClassSave(BdfObject bdf)
		{
			BdfNamedList nl = bdf.getNamedList();
			
			nl.set("name", bdf.newObject().setString(name));
			
			if(age != -1) {
				nl.set("age", bdf.newObject().setAutoInt(age));
			}
			
			if(type != -1) {
				nl.set("type", bdf.newObject().setAutoInt(type));
			}
		}
		
	}
	
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
		BdfReader reader = new BdfReader();
		BdfObject bdf = reader.getObject();
		
		bdf.getKeyLocation("type");
		bdf.getKeyLocation("qwerty");
		bdf.getKeyLocation("age");
		bdf.getKeyLocation("name");
		
		Storage[] items = {
				new Storage("test1", -1, 1),
				new Storage("test2", 69, 2),
				new Storage("test3", 420, -1),
				new Storage("test4", 23, 3),
				new Storage("test5", -1, -1),
		};
		
		BdfArray array = bdf.newArray(5);
		bdf.setArray(array);
		
		for(int i=0;i<items.length;i++) {
			items[i].BdfClassSave(array.get(i));
		}
		
		reader.serializeHumanReadable(System.out);
		
		IBdfDatabase db = reader.serialize();
		displayHex(db);
		
		reader = new BdfReader(db);
		
		reader.serializeHumanReadable(System.out);
	}

}
