package bdf;

import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfFileManager bdf = new BdfFileManager("db/file.db");
		
		BdfNamedList bdf_nl = new BdfNamedList();
		bdf_nl.set("greeting", BdfObject.getNew("Hello, World!"));
		bdf_nl.set("integer", BdfObject.getNew(21));
		bdf_nl.set("integer", BdfObject.getNew(52));
		bdf_nl.set("\"test\"", BdfObject.getNew((byte) 69));
		
		BdfArray bdf_array = new BdfArray();
		bdf_array.add(BdfObject.getNew(61));
		bdf_array.add(BdfObject.getNew(42.0d));
		bdf_array.add(BdfObject.getNew(67F));
		bdf_array.add(BdfObject.getNew("hello!"));
		bdf_array.add(BdfObject.getNew());
		bdf_array.add(BdfObject.getNew("\"hi\""));
		
		bdf_nl.set("array", BdfObject.getNew(bdf_array));
		bdf.setNamedList(bdf_nl);
		
		bdf.saveDatabase();
		
		System.out.println(bdf.serializeHumanReadable());
	}

}
