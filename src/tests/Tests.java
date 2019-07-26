package tests;

import java.util.Iterator;

import bdf.classes.BdfClassManager;
import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfFileManager bdf = new BdfFileManager("db.bdf");
		BdfNamedList nl = bdf.getNamedList();
		
		BdfObject class1 = nl.get("class1");
		BdfObject class2 = nl.get("class2");
		
		TestClass t1 = new TestClass();
		BdfClassManager m1 = new BdfClassManager(t1);
		m1.setBdf(class1);
		
		TestClass t2 = new TestClass();
		BdfClassManager m2 = new BdfClassManager(t2);
		m2.setBdf(class2);
		
		m1.load();
		m2.load();
		
		t1.tick();
		t2.tick();
		
		m1.save();
		m2.save();
		
		bdf.saveDatabase();
		
		System.out.println(bdf.serializeHumanReadable());
		
		BdfArray a = new BdfArray();
		
		a.add(BdfObject.withInteger(1));
		a.add(BdfObject.withInteger(534));
		a.add(BdfObject.withInteger(32));
		a.add(BdfObject.withInteger(22));
		a.add(BdfObject.withInteger(12));
		
		Iterator<BdfObject> i = a.iterator();
		
		while(i.hasNext())
		{
			System.out.println(i.next().getInteger());
			i.remove();
		}
		
		Iterator<BdfObject> i2 = a.iterator();
		
		while(i2.hasNext())
		{
			System.out.println(i2.next().getInteger());
		}
	}

}
