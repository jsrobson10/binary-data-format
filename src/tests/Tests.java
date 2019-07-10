package tests;

import bdf.classes.BdfClassManager;
import bdf.file.BdfFileManager;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;
import bdf.types.BdfTypes;

public class Tests {

	public static void main(String[] args)
	{
		BdfFileManager bdf = new BdfFileManager("db.bdf");
		
		if(bdf.getType() != BdfTypes.NAMED_LIST)
			bdf.setNamedList(new BdfNamedList());
		
		if(!bdf.getNamedList().contains("class1"))
			bdf.getNamedList().set("class1", BdfObject.with(new BdfNamedList()));
		
		if(!bdf.getNamedList().contains("class2"))
			bdf.getNamedList().set("class2", BdfObject.with(new BdfNamedList()));
		
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
