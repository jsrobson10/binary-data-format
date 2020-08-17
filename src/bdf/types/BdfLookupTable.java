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
	public int serialize(IBdfDatabase database, int[] locations, int[] map, byte flags)
	{
		int upto = 0;
		
		for(int i=0;i<map.length;i++)
		{
			byte[] key = keys.get(map[i]);
			
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
		
		for(int i=0;i<keys.size();i++)
		{
			// Skip this key if the location is unset (the key has been culled)
			if(locations[i] == -1) {
				continue;
			}
			
			size += keys.get(i).length;
			size += 1;
		}
		
		return size;
	}
	
	// Bubble sort
	private int[] sortLocations(int[] locations, int[] uses, int[] map)
	{
		int[] map_copy = new int[map.length];
		
		for(int i=0;i<map.length;i++) {
			map_copy[i] = map[i];
		}
		
		for(int i=0; i < map.length; i++)
		{
			boolean changed = false;
			
			for(int j=0; j < map.length - i - 1; j++)
			{
				int loc_0 = map[j];
				int loc_1 = map[j + 1];
				
				// Swap the index at j+1 and j in locations and uses
				if(uses[loc_1] > uses[loc_0])
				{
					int v_l = locations[loc_0];
					locations[loc_0] = locations[loc_1];
					locations[loc_1] = v_l;
					
					int v_u = uses[loc_0];
					uses[loc_0] = uses[loc_1];
					uses[loc_1] = v_u;
					
					int v_m = map_copy[j];
					map_copy[j] = map_copy[j+1];
					map_copy[j+1] = v_m;
					
					changed = true;
				}
			}
			
			if(!changed) {
				return map_copy;
			}
		}
		
		return map_copy;
	}
	
	public int[] serializeGetLocations(int[] locations)
	{
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
		
		int[] map = new int[next];
		next = 0;
		
		for(int i=0;i<locations.length;i++)
		{
			if(locations[i] != -1) {
				map[next] = i;
				next += 1;
			}
		}
		
		return sortLocations(locations, uses, map);
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
