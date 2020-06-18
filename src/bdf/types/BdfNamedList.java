package bdf.types;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

public class BdfNamedList implements IBdfType
{
	protected class Element
	{
		public byte[] key;
		public BdfObject object;
	}
	
	protected ArrayList<Element> elements = new ArrayList<Element>();
	
	public BdfNamedList() {
	}

	public BdfNamedList(IBdfDatabase data)
	{
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.size())
		{
			// Get the key
			int key_size = DataHelpers.getByteBuffer(data.getPointer(i, 4)).getInt();
			i += 4;
			byte[] key = data.getPointer(i, key_size).getBytes();
			
			// Get the object
			i += key_size;
			int object_size = DataHelpers.getByteBuffer(data.getPointer(i, 4)).getInt();
			i += 4;
			BdfObject object = new BdfObject(data.getPointer(i, object_size));
			
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
	public int serialize(IBdfDatabase database)
	{
		int pos = 0;
		
		for(Element o : elements)
		{
			database.setBytes(pos, DataHelpers.serializeInt(o.key.length));
			
			pos += 4;
			
			database.setBytes(pos, o.key);
			
			pos += o.key.length;
			
			int size = o.object.serialize(database.getPointer(pos + 4, database.size() - (pos + 4)));
			
			database.setBytes(pos, DataHelpers.serializeInt(size));
			
			pos += 4;
			pos += size;
		}
		
		return pos;
	}
	
	@Override
	public int serializeSeeker()
	{
		int size = 0;
		
		for(Element o : elements)
		{
			size += 8;
			size += o.key.length;
			size += o.object.serializeSeeker();
		}
		
		return size;
	}
	
	@Override
	public String serializeHumanReadable(BdfIndent indent, int it)
	{
		if(elements.size() == 0) {
			return "{}";
		}
		
		String data = "{";
		
		for(int i=0;i<elements.size();i++)
		{
			Element e = elements.get(i);
			
			data += indent.breaker;
			
			for(int n=0;n<=it;n++) {
				data += indent.indent;
			}
			
			data += DataHelpers.serializeString(new String(e.key, StandardCharsets.UTF_8));
			data += ": ";
			data += e.object.serializeHumanReadable(indent, it + 1);
			
			if(elements.size() > i+1)
			{
				data += ", ";
			}
		}
		
		data += indent.breaker;
		
		for(int n=0;n<it;n++) {
			data += indent.indent;
		}
		
		return data + "}";
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
			if(DataHelpers.bytesAreEqual(e.key, key_bytes))
			{
				// Set the object
				object = e.object;
				
				// Return the object
				return object;
			}
		}
		
		// Get a bdf object
		BdfObject o = new BdfObject();
		
		// Set the bdf object
		this.set(key, o);
		
		// Send back the object
		return o;
	}
	
	public BdfNamedList remove(String key)
	{
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements
		for(int i=0;i<elements.size();i++)
		{
			// Get the element
			Element e = elements.get(i);
			
			// Is the specified key the same as the elements key
			if(DataHelpers.bytesAreEqual(e.key, key_bytes))
			{
				// Delete this element
				elements.remove(i);
				
				// Exit out of the function, prevent NullPointException
				return this;
			}
		}
		
		// Send back nothing
		return this;
	}
	
	public BdfNamedList remove(BdfObject bdf)
	{
		for(int i=0;i<elements.size();i++) {
			if(elements.get(i).object == bdf) {
				elements.remove(i);
				i -= 1;
			}
		}
		
		return this;
	}
	
	public BdfNamedList set(String key, BdfObject object)
	{
		// Convert the key to bytes
		byte[] key_bytes = key.getBytes();
		
		// Loop over the elements, does it already exist
		for(Element e : elements)
		{
			// Is the key here the same as the specified key
			if(DataHelpers.bytesAreEqual(e.key, key_bytes))
			{
				// Set the new object
				e.object = object;
				
				// Exit out of the function, don't add another object
				return this;
			}
		}
		
		// Create a new element object
		Element e = new Element();
		e.key = key_bytes;
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
			if(DataHelpers.bytesAreEqual(e.key, key_bytes))
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
			keys[i] = new String(e.key, StandardCharsets.UTF_8);
		}
		
		// Return the list of keys as strings
		return keys;
	}
	
	public int size() {
		return elements.size();
	}
}
