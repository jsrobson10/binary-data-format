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
		if(bdf.getType() != BdfTypes.NAMED_LIST) bdf.setNamedList(new BdfNamedList());
		BdfNamedList nl = bdf.getNamedList();
		this.i = nl.contains("i") ? nl.get("i").getInteger() : 0;
	}

	@Override
	public void BdfClassSave(BdfObject bdf)
	{
		bdf.setNamedList(new BdfNamedList());
		bdf.getNamedList().set("i", BdfObject.with(i));
	}

	public void tick()
	{
		System.out.println(i);
		i++;
	}
	
}
