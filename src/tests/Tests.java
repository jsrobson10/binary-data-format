package tests;

import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfNamedList nl = new BdfNamedList();
		
		float[] array = {0.1F, 5.3F, 42.0F};
		nl.set("array", BdfObject.withFloatArray(array));
		nl.set("string", BdfObject.withString("Hello, World!"));
		
		System.out.println(BdfObject.withNamedList(nl).serializeHumanReadable());
		
		array[1] = 8.9F;
		
		System.out.println(BdfObject.withNamedList(nl).serializeHumanReadable());
		nl.set("array", BdfObject.withFloatArray(array));
		
		System.out.println(BdfObject.withNamedList(nl).serializeHumanReadable());
		BdfObject bdf = new BdfObject(new BdfDatabase(BdfObject.withNamedList(nl).serialize().getBytes()));
		nl = bdf.getNamedList();
		
		byte[] array2 = nl.get("array").getByteArray();
		
		for(byte i : array2) {
			System.out.println(i);
		}
		
		System.out.println(nl.get("string").getString());
		System.out.println(bdf.serializeHumanReadable());
	}

}
