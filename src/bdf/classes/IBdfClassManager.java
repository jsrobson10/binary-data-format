package bdf.classes;

import bdf.types.BdfNamedList;

public interface IBdfClassManager
{
	public void BdfClassLoad(BdfNamedList bdf);
	public void BdfClassSave(BdfNamedList bdf);
}
