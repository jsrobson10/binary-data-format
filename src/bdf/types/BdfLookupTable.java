package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

class BdfLookupTable implements IBdfType
{
	private ArrayList<byte[]> keys;
	private BdfReader reader;
	
	BdfLookupTable(BdfReader reader)
	{
		this.keys = new ArrayList<byte[]>();
		this.reader = reader;
	}
	
	BdfLookupTable(BdfReader reader, IBdfDatabase database)
	{
		this.keys = new ArrayList<byte[]>();
		this.reader = reader;
		
		for(int i=0;i<database.size();)
		{
			int key_size = 0xff & database.getByte(i);
			i += 1;
			
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
	public int serialize(IBdfDatabase database, int[] locations, byte flags)
	{
		int upto = 0;
		
		for(int i=0;i<locations.length;i++)
		{
			int loc = locations[i];
			
			if(loc == -1) {
				continue;
			}
			
			byte[] key = keys.get(i);
			
			database.setBytes(key, upto + 1);
			database.setByte(upto, (byte)key.length);
			
			upto += key.length;
			upto += 1;
		}
		
		return upto;
	}

	@Override
	public int serializeSeeker(int[] locations)
	{
		int size = 0;
		
		for(int i=0;i<locations.length;i++)
		{
			// Skip this key if the location is unset (the key has been culled)
			int loc = locations[i];
			
			if(loc == -1) {
				continue;
			}
			
			size += keys.get(i).length;
			size += 1;
		}
		
		return size;
	}
	
	public int[] serializeGetLocations()
	{
		int[] locations = new int[keys.size()];
		int[] uses = new int[keys.size()];
		int next = 0;
		
		reader.bdf.getLocationUses(uses);
		
		for(int i=0;i<locations.length;i++)
		{
			if(uses[i] > 0) {
				locations[i] = next;
				next += 1;
			} else {
				locations[i] = -1;
			}
		}

		return locations;
	}

	@Override
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) {
	}
	
	@Override
	public void getLocationUses(int[] locations) {
	}

	public int size() {
		return keys.size();
	}
}
