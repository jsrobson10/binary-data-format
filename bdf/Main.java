package bdf;

import bdf.data.BdfDatabase;
import bdf.types.BdfArray;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Main {

	public static void main(String[] args)
	{
		
		BdfObject object = BdfObject.getNew(new BdfNamedList());
		
		for(int i=0;i<1000;i++)
		{
			object.getNamedList().set(Integer.toString(i), BdfObject.getNew("value + 1 = "+Integer.toString(i+1)));
		}
		
		byte[] database = object.serialize().getBytes();
		BdfObject object2 = new BdfObject(new BdfDatabase(database));
		
		System.out.println("value = \""+ object2.getNamedList().get("20").getString()+"\"");
		System.out.println(object2.getNamedList().contains("54"));
		
	}

}
