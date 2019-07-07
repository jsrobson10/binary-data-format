package bdf;

import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfObject;

public class Main {

	public static void main(String[] args)
	{
		
		BdfFileManager file = new BdfFileManager("db.bdf");
		
		
		file.setArray(new BdfArray());
		file.getArray().add(BdfObject.getNew("Hello"));
		System.out.println(file.getArray().get(0).getString());
		file.saveDatabase();
		
		
		BdfFileManager file2 = new BdfFileManager("db.bdf");
		System.out.println(file2.getArray().get(0).getString());
		
	}

}
