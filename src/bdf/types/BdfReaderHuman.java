package bdf.types;

import bdf.data.BdfStringPointer;
import bdf.util.BdfError;

public class BdfReaderHuman extends BdfReader
{
	public BdfReaderHuman(String data)
	{
		BdfStringPointer ptr = new BdfStringPointer(data.toCharArray(), 0);
		
		ptr.ignoreBlanks();
		bdf = new BdfObject(lookupTable, ptr);
		
		try {
			ptr.ignoreBlanks();
		}
		
		catch(BdfError e) {
			if(e.getType() == BdfError.ERROR_END_OF_FILE) {
				return;
			} else {
				throw e;
			}
		}
		
		throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
	}
}
