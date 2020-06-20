package tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bdf.data.BdfDatabase;
import bdf.file.BdfCompressedFileManager;
import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfIndent;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args) throws InterruptedException, IOException
	{
		/*
		BdfObject bdf = new BdfObject();
		BdfNamedList nl = bdf.getNamedList();
		
		byte[] bytes = new byte[1024*1024*1024];
		for(int i=0;i<bytes.length;i++) {
			bytes[i] = (byte)0;
		}
		
		for(int i=0;i<1000;i++) {
			nl = nl.get("next").getNamedList();
		}
		
		nl.get("next").setByteArray(bytes);
		
		BdfDatabase data = bdf.serialize();
		
		FileOutputStream file = new FileOutputStream("./database.bdf");
		data.writeToStream(file);
		*/
		
		
		BdfObject bdf = new BdfObject();
		BdfArray a = bdf.getArray();
		
		byte[] bytes = new byte[1024*1024*1024/2];
		for(int i=0;i<bytes.length;i++) {
			bytes[i] = (byte)0;
		}
		
		for(int i=0;i<10;i++) {
			BdfArray a2 = new BdfArray();
			a.add(BdfObject.withArray(a2));
			a = a2;
		}
		
		a.add(BdfObject.withByteArray(bytes));
		
		BdfDatabase data = bdf.serialize();
		
		FileOutputStream file = new FileOutputStream("./database.bdf");
		data.writeToStream(file);
		
		
		//BdfFileManager bdf = new BdfFileManager("./database.bdf");
		//System.out.println("Loaded bdf");
		//Thread.sleep(5000);
	}

}
