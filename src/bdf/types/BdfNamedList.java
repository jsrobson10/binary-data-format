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
			int object_size = BdfObject.getSize(data.getPointer(i));
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
			
			int size = o.object.serialize(database.getPointer(pos + 4), locations);
			
			pos += size + 4;
		}
		
		return pos;
	}
	
	@Override
	public int serializeSeeker(int[] locations)
	{
		int size = 0;
		
		for(Element o : elements)
		{
			size += 4;
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
	
	public BdfObject get(int key)
	{
		// Get the object to send back
		BdfObject object = null;
		
		// Loop over the elements
		for(Element e : elements)
		{
			// Is this the element key
			if(e.key == key)
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
	
	public BdfObject remove(int key)
	{
		// Loop over the elements
		for(int i=0;i<elements.size();i++)
		{
			// Get the element
			Element e = elements.get(i);
			
			// Is the specified key the same as the elements key
			if(e.key == key) {
				return elements.remove(i).object;
			}
		}
		
		// Send back nothing
		return null;
	}
	
	public BdfNamedList set(int key, BdfObject object)
	{
		// Loop over the elements, does it already exist
		for(Element e : elements)
		{
			// Is the key here the same as the specified key
			if(e.key == key)
			{
				// Set the new object
				e.object = object;
				
				// Exit out of the function, don't add another object
				return this;
			}
		}
		
		// Create a new element object
		Element e = new Element();
		e.object = object;
		e.key = key;
		
		// Add the new element object to the elements list
		elements.add(e);
		
		// Send this class back
		return this;
	}
	
	public boolean contains(int key)
	{
		// Loop over the elements
		for(Element e : elements)
		{
			// Is the elements key the same as the specified key
			if(e.key == key)
			{
				// Send back true to say the element was found
				return true;
			}
		}
		
		// Send back false if nothing was found
		return false;
	}
	
	public int[] getKeys()
	{
		// Get the keys to send back
		int[] keys = new int[elements.size()];
		
		// Loop over the elements
		for(int i=0;i<elements.size();i++)
		{
			// Get the element
			Element e = elements.get(i);
			keys[i] = e.key;
		}
		
		// Return the list of keys as strings
		return keys;
	}
	
	public int getKeyLocation(String key) {
		return lookupTable.getLocation(key.getBytes());
	}
	
	public String getKeyName(int key) {
		return new String(lookupTable.getName(key));
	}
	
	public boolean contains(String key) {
		return contains(lookupTable.getLocation(key.getBytes()));
	}
	
	public BdfNamedList set(String key, BdfObject object) {
		return set(lookupTable.getLocation(key.getBytes()), object);
	}
	
	public BdfObject remove(String key) {
		return remove(lookupTable.getLocation(key.getBytes()));
	}
	
	public BdfObject get(String key) {
		return get(lookupTable.getLocation(key.getBytes()));
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
