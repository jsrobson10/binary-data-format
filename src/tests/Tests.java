package tests;

import bdf.file.BdfCompressedFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfIndent;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfCompressedFileManager bdf = new BdfCompressedFileManager("./db.bdf");
		
		BdfNamedList nl = bdf.getNamedList();
		
		int array[] = {1,2,3,6,7,0};
		int array2[] = {1,2,3,6,7,0};
		
		BdfArray array_bdf = new BdfArray();
		
		array_bdf.add(BdfObject.withBoolean(true));
		array_bdf.add(BdfObject.withBoolean(false));
		array_bdf.add(BdfObject.withInteger(7));
		array_bdf.add(BdfObject.withNamedList());
		array_bdf.add(BdfObject.withArray());
		array_bdf.add(BdfObject.withIntegerArray(array2));
		
		nl.set("it", BdfObject.withInteger(nl.get("it").getInteger() + 1));
		nl.set("int_array", BdfObject.withIntegerArray(array));
		nl.set("array", BdfObject.withArray(array_bdf));
		
		System.out.println(bdf.serializeHumanReadable(new BdfIndent("\t", "\n")));
		
		bdf.saveDatabase();
	}

}
