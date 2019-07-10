package tests;

import bdf.classes.IBdfClassManager;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class TestClass implements IBdfClassManager
{
	int i = 0;
	
	@Override
	public void BdfClassLoad(BdfNamedList bdf) {
		this.i = bdf.contains("i") ? bdf.get("i").getInteger() : 0;
	}

	@Override
	public void BdfClassSave(BdfNamedList bdf) {
		bdf.set("i", BdfObject.with(i));
	}

	public void tick()
	{
		System.out.println(i);
		i++;
	}
	
}
