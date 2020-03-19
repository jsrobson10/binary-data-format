package tests;

import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.file.BdfCompressedFileManager;
import bdf.file.BdfFileManager;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfCompressedFileManager bdf = new BdfCompressedFileManager("./db.bdf");
		
		BdfNamedList nl = bdf.getNamedList();
		
		nl.set("it", BdfObject.withInteger(nl.get("it").getInteger() + 1));
		
		System.out.println(bdf.serializeHumanReadable());
		
		bdf.saveDatabase();
	}

}
