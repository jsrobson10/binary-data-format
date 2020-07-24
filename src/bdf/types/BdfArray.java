package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

public class BdfArray implements IBdfType, Iterable<BdfObject>
{
	protected ArrayList<BdfObject> elements = new ArrayList<BdfObject>();
	protected BdfLookupTable lookupTable;
	
	BdfArray(BdfLookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}
	
	BdfArray(BdfLookupTable lookupTable, IBdfDatabase data)
	{
		this.lookupTable = lookupTable;
		
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.size())
		{
			// Get the size of the object
			int size = DataHelpers.getByteBuffer(data.getPointer(i, Integer.BYTES)).getInt();
			
			// Get the object
			BdfObject object = new BdfObject(lookupTable, data.getPointer((i+Integer.BYTES), size));
			
			// Add the object to the elements list
			elements.add(object);
			
			// Increase the iterator by the amount of bytes
			i += Integer.BYTES+size;
		}
	}
	
	public BdfObject createObject() {
		return new BdfObject(lookupTable);
	}
	
	public BdfNamedList createNamedList() {
		return new BdfNamedList(lookupTable);
	}
	
	public BdfArray createArray() {
		return new BdfArray(lookupTable);
	}
	
	@Override
	public int serializeSeeker()
	{
		int size = 0;
		
		for(BdfObject o : elements) {
			size += o.serializeSeeker();
			size += 4;
		}
		
		return size;
	}
	
	@Override
	public int serialize(IBdfDatabase database)
	{
		int pos = 0;
		
		for(BdfObject o : elements)
		{
			int size = o.serialize(database.getPointer(pos + 4));
			database.setBytes(pos, DataHelpers.serializeInt(size));
			
			pos += size;
			pos += 4;
		}
		
		return pos;
	}
	
	@Override
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) throws IOException
	{
		if(elements.size() == 0) {
			stream.write("[]".getBytes());
			return;
		}
		
		stream.write('[');
		
		for(int i=0;i<elements.size();i++)
		{
			BdfObject o = elements.get(i);
			
			stream.write(indent.breaker.getBytes());
			
			for(int n=0;n<=it;n++) {
				stream.write(indent.indent.getBytes());
			}
			
			o.serializeHumanReadable(stream, indent, it + 1);
			
			if(elements.size() > i+1) {
				stream.write(", ".getBytes());
			}
		}
		
		stream.write(indent.breaker.getBytes());
		
		for(int n=0;n<it;n++) {
			stream.write(indent.indent.getBytes());
		}
		
		stream.write(']');
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
	
	public BdfObject remove(int index) {
		BdfObject bdf = elements.get(index);
		elements.remove(index);
		return bdf;
	}
	
	public BdfArray remove(BdfObject bdf) {
		elements.remove(bdf);
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
