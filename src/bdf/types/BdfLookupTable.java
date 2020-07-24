package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

class BdfLookupTable implements IBdfType
{
	private ArrayList<String> keys;
	
	BdfLookupTable() {
		keys = new ArrayList<String>();
	}
	
	BdfLookupTable(IBdfDatabase database)
	{
		keys = new ArrayList<String>();
		
		for(int i=0;i<database.size();)
		{
			int key_size = DataHelpers.getByteBuffer(database.getPointer(i, 4)).getInt();
			i += 4;
			
			String key = new String(database.getBytes(i, key_size), StandardCharsets.UTF_16);
			keys.add(key);
			
			i += key_size;
		}
	}
	
	int getLocation(String name)
	{
		for(int i=0;i<keys.size();i++)
		{
			String key = keys.get(i);
			if(key.contentEquals(name)) {
				return i;
			}
		}
		
		keys.add(name);
		return keys.size() - 1;
	}
	
	String getName(int location) {
		return keys.get(location);
	}

	@Override
	public int serialize(IBdfDatabase database)
	{
		int upto = 0;
		
		for(int i=0;i<keys.size();i++)
		{
			String key = keys.get(i);
			
			database.setBytes(upto + 4, key.getBytes());
			database.setBytes(upto, DataHelpers.serializeInt(key.length()));
			
			upto += key.length();
			upto += 4;
		}
		
		return upto;
	}

	@Override
	public int serializeSeeker()
	{
		int size = 0;
		
		for(int i=0;i<keys.size();i++) {
			size += keys.get(i).length();
			size += 4;
		}
		
		return size;
	}

	@Override
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) {
	}
}
