package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

public class BdfNamedList implements IBdfType
{
	protected class Element
	{
		public int key;
		public BdfObject object;
	}
	
	protected ArrayList<Element> elements = new ArrayList<Element>();
	protected BdfLookupTable lookupTable;
	
	BdfNamedList(BdfLookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}

	BdfNamedList(BdfLookupTable lookupTable, IBdfDatabase data)
	{
		this.lookupTable = lookupTable;
		
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.size())
		{
			// Get the key
			int key = DataHelpers.getByteBuffer(data.getPointer(i, 4)).getInt();
			i += 4;
			
			// Get the object
			int object_size = DataHelpers.getByteBuffer(data.getPointer(i, 4)).getInt();
			i += 4;
			BdfObject object = new BdfObject(lookupTable, data.getPointer(i, object_size));
			
			// Create a new element and save some data to it
			Element element = new Element();
			element.object = object;
			element.key = key;
			
			// Add the object to the elements list
			elements.add(element);
			
			// Increase the iterator by the amount of bytes
			i += object_size;
		}
	}
	
	@Override
	public int serialize(IBdfDatabase database, int[] locations)
	{
		int pos = 0;
		
		for(Element o : elements)
		{
			database.setBytes(pos, DataHelpers.serializeInt(locations[o.key]));
			
			pos += 4;
			
			int size = o.object.serialize(database.getPointer(pos + 4), locations);
			
			database.setBytes(pos, DataHelpers.serializeInt(size));
			
			pos += 4;
			pos += size;
		}
		
		return pos;
	}
	
	@Override
	public int serializeSeeker(int[] locations)
	{
		int size = 0;
		
		for(Element o : elements)
		{
			size += 8;
			size += o.object.serializeSeeker(locations);
		}
		
		return size;
	}
	
	@Override
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) throws IOException
	{
		if(elements.size() == 0) {
			stream.write("{}".getBytes());
			return;
		}

		stream.write('{');
		
		for(int i=0;i<elements.size();i++)
		{
			Element e = elements.get(i);
			
			stream.write(indent.breaker.getBytes());
			
			for(int n=0;n<=it;n++) {
				stream.write(indent.indent.getBytes());
			}
			
			stream.write((DataHelpers.serializeString(new String(lookupTable.getName(e.key))) + ": ").getBytes());
			e.object.serializeHumanReadable(stream, indent, it + 1);
			
			if(elements.size() > i+1) {
				stream.write(", ".getBytes());
			}
		}
		
		stream.write(indent.breaker.getBytes());
		
		for(int n=0;n<it;n++) {
			stream.write(indent.indent.getBytes());
		}
		
		stream.write('}');
	}
	
	public BdfObject get(String key)
	{
		// Get the object to send back
		BdfObject object = null;
		
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements
		for(Element e : elements)
		{
			// Is this the element key
			if(DataHelpers.bytesAreEqual(lookupTable.getName(e.key), key_bytes))
			{
				// Set the object
				object = e.object;
				
				// Return the object
				return object;
			}
		}
		
		// Get a bdf object
		BdfObject o = new BdfObject(lookupTable);
		
		// Set the bdf object
		this.set(key, o);
		
		// Send back the object
		return o;
	}
	
	public BdfObject remove(String key)
	{
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements
		for(int i=0;i<elements.size();i++)
		{
			// Get the element
			Element e = elements.get(i);
			
			// Is the specified key the same as the elements key
			if(DataHelpers.bytesAreEqual(lookupTable.getName(e.key), key_bytes)) {
				return elements.remove(i).object;
			}
		}
		
		// Send back nothing
		return null;
	}
	
	public BdfNamedList set(String key, BdfObject object)
	{
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements, does it already exist
		for(Element e : elements)
		{
			// Is the key here the same as the specified key
			if(DataHelpers.bytesAreEqual(lookupTable.getName(e.key), key_bytes))
			{
				// Set the new object
				e.object = object;
				
				// Exit out of the function, don't add another object
				return this;
			}
		}
		
		// Create a new element object
		Element e = new Element();
		e.key = lookupTable.getLocation(key_bytes);
		e.object = object;
		
		// Add the new element object to the elements list
		elements.add(e);
		
		// Send this class back
		return this;
	}
	
	public boolean contains(String key)
	{
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements
		for(Element e : elements)
		{
			// Is the elements key the same as the specified key
			if(DataHelpers.bytesAreEqual(lookupTable.getName(e.key), key_bytes))
			{
				// Send back true to say the element was found
				return true;
			}
		}
		
		// Send back false if nothing was found
		return false;
	}
	
	public String[] getKeys()
	{
		// Get the keys to send back
		String[] keys = new String[elements.size()];
		
		// Loop over the elements
		for(int i=0;i<elements.size();i++)
		{
			// Get the element
			Element e = elements.get(i);
			keys[i] = new String(lookupTable.getName(e.key));
		}
		
		// Return the list of keys as strings
		return keys;
	}
	
	public int size() {
		return elements.size();
	}
	
	@Override
	public void getLocationUses(int[] locations)
	{
		for(Element e : elements) {
			locations[e.key] += 1;
			e.object.getLocationUses(locations);
		}
	}
}
