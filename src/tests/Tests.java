package tests;

import java.io.IOException;

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
		nl.set("world", nl.createObject().setInteger(420));
		
		reader.serializeHumanReadable(System.out, new BdfIndent("  ", "\n"));
	}

}
