package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

class BdfLookupTable implements IBdfType
{
	private ArrayList<byte[]> keys;
	
	BdfLookupTable() {
		keys = new ArrayList<byte[]>();
	}
	
	BdfLookupTable(IBdfDatabase database)
	{
		keys = new ArrayList<byte[]>();
		
		for(int i=0;i<database.size();)
		{
			int key_size = DataHelpers.getByteBuffer(database.getPointer(i, 4)).getInt();
			i += 4;
			
			keys.add(database.getBytes(i, key_size));
			
			i += key_size;
		}
	}
	
	int getLocation(byte[] name)
	{
		for(int i=0;i<keys.size();i++)
		{
			if(DataHelpers.bytesAreEqual(name, keys.get(i))) {
				return i;
			}
		}
		
		keys.add(name);
		return keys.size() - 1;
	}
	
	byte[] getName(int location) {
		return keys.get(location);
	}

	@Override
	public int serialize(IBdfDatabase database)
	{
		int upto = 0;
		
		for(int i=0;i<keys.size();i++)
		{
			byte[] key = keys.get(i);
			
			database.setBytes(upto + 4, key);
			database.setBytes(upto, DataHelpers.serializeInt(key.length));
			
			upto += key.length;
			upto += 4;
		}
		
		return upto;
	}

	@Override
	public int serializeSeeker()
	{
		int size = 0;
		
		for(int i=0;i<keys.size();i++) {
			size += keys.get(i).length;
			size += 4;
		}
		
		return size;
	}

	@Override
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) {
	}
}
