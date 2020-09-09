package bdf.types;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import bdf.data.BdfStringPointer;
import bdf.data.IBdfDatabase;
import bdf.util.BdfError;

public class BdfArray implements IBdfType, Iterable<BdfObject>
{
	protected ArrayList<BdfObject> elements;
	
	BdfArray(BdfLookupTable lookupTable, BdfStringPointer ptr)
	{
		this.elements = new ArrayList<BdfObject>();
		
		ptr.increment();
		
		// [..., ...]
		while(true)
		{
			ptr.ignoreBlanks();
			
			if(ptr.getChar() == ']') {
				ptr.increment();
				return;
			}
			
			add(new BdfObject(lookupTable, ptr));
			
			// There should be a comma after this
			ptr.ignoreBlanks();
			
			char c = ptr.getChar();
			
			if(c == ']') {
				ptr.increment();
				return;
			}
			
			if(c != ',') {
				throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
			}
			
			ptr.increment();
			ptr.ignoreBlanks();
		}
	}
	
	BdfArray(BdfLookupTable lookupTable, int size)
	{
		this.elements = new ArrayList<BdfObject>(size);
		
		for(int i=0;i<size;i++) {
			this.elements.add(new BdfObject(lookupTable));
		}
	}
	
	BdfArray(BdfLookupTable lookupTable, IBdfDatabase data)
	{
		this.elements = new ArrayList<BdfObject>();
		
		// Create an iterator value to loop over the data
		int i = 0;
		
		// Loop over the data
		while(i < data.size())
		{
			// Get the size of the object
			int size = BdfObject.getSize(data.getPointer(i));
			
			if(size <= 0 || i + size > data.size()) {
				return;
			}
			
			// Get the object
			BdfObject object = new BdfObject(lookupTable, data.getPointer(i, size));
			
			// Add the object to the elements list
			elements.add(object);
			
			// Increase the iterator by the amount of bytes
			i += size;
		}
	}
	
	@Override
	public int serializeSeeker(int[] locations)
	{
		int size = 0;
		
		for(BdfObject o : elements) {
			size += o.serializeSeeker(locations);
		}
		
		return size;
	}
	
	@Override
	public int serialize(IBdfDatabase database, int[] locations, byte flags)
	{
		int pos = 0;
		
		for(BdfObject o : elements) {
			pos += o.serialize(database.getPointer(pos), locations, (byte)0);
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
	
	@Override
	public void getLocationUses(int[] locations) {
		for(BdfObject element : elements) {
			element.getLocationUses(locations);
		}
	}

}
