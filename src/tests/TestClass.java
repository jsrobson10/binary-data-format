package tests;

import bdf.classes.IBdfClassManager;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;
import bdf.types.BdfTypes;

public class TestClass implements IBdfClassManager
{
	int i = 0;
	
	@Override
	public void BdfClassLoad(BdfObject bdf)
	{
		BdfNamedList nl = bdf.getNamedList();
		this.i = nl.get("i").getInteger();
	}

	@Override
	public void BdfClassSave(BdfObject bdf)
	{
		BdfNamedList nl = new BdfNamedList();
		nl.set("i", BdfObject.withInteger(i));
		bdf.setNamedList(nl);
	}

	public void tick()
	{
		System.out.println(i);
		i++;
	}
	
}
