package bdf.types;

import java.util.ArrayList;
import java.util.Iterator;

import bdf.data.BdfDatabase;
import bdf.util.DataHelpers;

public class BdfArray implements IBdfType, Iterable<BdfObject>
{
	protected ArrayList<BdfObject> elements = new ArrayList<BdfObject>();
	
	public BdfArray() {
	}
	
	public BdfArray(BdfDatabase data)
	{
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.length())
		{
			// Get the size of the object
			int size = DataHelpers.getByteBuffer(data.getAt(i, (i+(Integer.SIZE/8)))).getInt();
			
			// Get the object
			BdfObject object = new BdfObject(data.getAt((i+(Integer.SIZE/8)), (i+(Integer.SIZE/8)+size)));
			
			// Add the object to the elements list
			elements.add(object);
			
			// Increase the iterator by the amount of bytes
			i += (Integer.SIZE/8)+size;
		}
	}

	@Override
	public BdfDatabase serialize()
	{
		// Create the serialized data string
		BdfDatabase serialized = new BdfDatabase();
		
		// Loop over the elements
		for(BdfObject o : elements)
		{
			// Convert the object to a string
			BdfDatabase db = o.serialize();
			
			// Add the serialized object to the serialized data
			serialized = BdfDatabase.add(serialized, DataHelpers.serializeInt(db.length()));
			serialized = BdfDatabase.add(serialized, db);
		}
		
		// Send back the serialized data
		return serialized;
	}
	
	@Override
	public String serializeHumanReadable(BdfIndent indent, int it)
	{
		if(elements.size() == 0) {
			return "[]";
		}
		
		String data = "[";
		
		for(int i=0;i<elements.size();i++)
		{
			BdfObject o = elements.get(i);
			
			data += indent.breaker;
			
			for(int n=0;n<=it;n++) {
				data += indent.indent;
			}
			
			data += o.serializeHumanReadable(indent, it + 1);
			
			if(elements.size() > i+1)
			{
				data += ", ";
			}
		}
		
		data += indent.breaker;
		
		for(int n=0;n<it;n++) {
			data += indent.indent;
		}
		
		return data + "]";
	}
	
	public BdfArray add(BdfObject o)
	{
		// Add an element
		elements.add(o);
		
		return this;
	}
	
	public BdfArray clear()
	{
		// Clear the elements
		elements.clear();
		
		return this;
	}
	
	public BdfArray remove(int index)
	{
		elements.remove(index);
		return this;
	}
	
	public BdfObject get(int index) {
		return elements.get(index);
	}
	
	public BdfArray set(int index, BdfObject element) {
		elements.set(index, element);
		return this;
	}
	
	public int size() {
		return elements.size();
	}

	@Override
	public Iterator<BdfObject> iterator()
	{
		return new Iterator<BdfObject>()
		{
			protected int i = 0;
			
			@Override
			public boolean hasNext()
			{
				return elements.size() > i;
			}

			@Override
			public BdfObject next()
			{
				BdfObject o = elements.get(i);
				
				i++;
				
				return o;
			}
			
			@Override
			public void remove()
			{
				i-=1;
				elements.remove(i);
			}
		};
	}

}
