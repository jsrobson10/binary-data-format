package bdf.classes;

import bdf.types.BdfObject;

public class BdfClassManager
{
	protected IBdfClassManager method;
	protected BdfObject object = new BdfObject();
	
	public BdfClassManager(IBdfClassManager method)
	{
		// Save some variables for later
		this.method = method;
	}
	
	public void setBdf(BdfObject bdf) {
		this.object = bdf;
	}
	
	public BdfObject getBdf() {
		return this.object;
	}
	
	public void save(BdfObject bdf) {
		method.BdfClassSave(bdf);
	}
	
	public void load(BdfObject bdf) {
		method.BdfClassLoad(bdf);
	}
	
	public void save() {
		this.save(this.object);
	}
	
	public void load() {
		this.load(this.object);
	}
}
