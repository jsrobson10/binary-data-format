package tests;

import bdf.classes.IBdfClassManager;
import bdf.types.BdfObject;

public class TestClass implements IBdfClassManager
{
	int i = 0;
	
	@Override
	public void BdfClassLoad(BdfObject bdf)
	{
		bdf.setNamedListIfInvalid();
		bdf.getNamedList().setIfUndefined("i", BdfObject.withInteger(0));
		this.i = bdf.getNamedList().get("i").getInteger();
	}

	@Override
	public void BdfClassSave(BdfObject bdf)
	{
		bdf.setNamedList();
		bdf.getNamedList().set("i", BdfObject.withInteger(i));
	}

	public void tick()
	{
		System.out.println(i);
		i++;
	}
	
}
