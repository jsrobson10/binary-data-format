package tests;

import bdf.classes.BdfClassManager;
import bdf.file.BdfFileManager;

public class Tests {

	public static void main(String[] args)
	{
		BdfFileManager bdf = new BdfFileManager("db.bdf");
		
		bdf.setNamedListIfInvalid();
		bdf.getNamedList().allocIfUndefined("class1");
		bdf.getNamedList().allocIfUndefined("class2");
		
		TestClass t1 = new TestClass();
		BdfClassManager m1 = new BdfClassManager(t1);
		m1.setBdf(bdf.getNamedList().get("class1"));
		
		TestClass t2 = new TestClass();
		BdfClassManager m2 = new BdfClassManager(t2);
		m2.setBdf(bdf.getNamedList().get("class2"));
		
		m1.load();
		m2.load();
		
		t1.tick();
		t2.tick();
		
		m1.save();
		m2.save();
		
		bdf.saveDatabase();
		
		System.out.println(bdf.serializeHumanReadable());
	}

}
