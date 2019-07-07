package bdf.types;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import bdf.data.BdfDatabase;
import bdf.exception.UndefinedKeyException;
import bdf.util.DataHelpers;

public class BdfNamedList implements IBdfType
{
	protected class Element
	{
		public byte[] key;
		public BdfObject object;
	}
	
	ArrayList<Element> elements = new ArrayList<Element>();
	
	public BdfNamedList() {
	}

	public BdfNamedList(BdfDatabase data)
	{
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.length())
		{
			// Get the key
			int key_size = DataHelpers.getByteBuffer(data.getAt(i, i+(Integer.SIZE/8))).getInt();
			i += (Integer.SIZE/8);
			byte[] key = data.getAt(i, i+key_size).getBytes();
			
			// Get the object
			i += key_size;
			int object_size = DataHelpers.getByteBuffer(data.getAt(i, i+(Integer.SIZE/8))).getInt();
			i += (Integer.SIZE/8);
			BdfObject object = new BdfObject(data.getAt(i, i+object_size));
			
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
	public BdfDatabase serialize()
	{
		// Create the serialized data string
		BdfDatabase serialized = new BdfDatabase();
		
		// Loop over the elements
		for(Element o : elements)
		{
			// Add the serialized data to the data string
			BdfDatabase data = o.object.serialize();
			serialized = BdfDatabase.add(serialized, DataHelpers.serializeInt(o.key.length));
			serialized = BdfDatabase.add(serialized, new BdfDatabase(o.key));
			serialized = BdfDatabase.add(serialized, DataHelpers.serializeInt(data.length()));
			serialized = BdfDatabase.add(serialized, data);
		}
		
		// Send back the serialized data
		return serialized;
	}
	
	@Override
	public String serializeHumanReadable()
	{
		String data = "{";
		
		for(int i=0;i<elements.size();i++)
		{
			Element e = elements.get(i);
			
			data += "\"";
			data += (new String(e.key, StandardCharsets.UTF_8)).replaceAll("\"", "\\\"");
			data += "\": ";
			data += e.object.serializeHumanReadable();
			
			if(elements.size() > i+1)
			{
				data += ", ";
			}
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
		
		// Raise an error
		throw new UndefinedKeyException(key);
	}
	
	public void remove(String key)
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
				return;
			}
		}
		
		// Raise an error
		throw new UndefinedKeyException(key);
	}
	
	public void set(String key, BdfObject object)
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
			}
		}
		
		// Create a new element object
		Element e = new Element();
		e.key = key_bytes;
		e.object = object;
		
		// Add the new element object to the elements list
		elements.add(e);
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
