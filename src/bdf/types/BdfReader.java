package bdf.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import bdf.data.BdfDatabase;
import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

public class BdfReader
{
	protected BdfLookupTable lookupTable;
	protected BdfObject bdf;
	
	public BdfReader() {
		lookupTable = new BdfLookupTable();
		bdf = new BdfObject(lookupTable);
	}
	
	public BdfReader(byte[] database) {
		this(new BdfDatabase(database));
	}
	
	public BdfReader(IBdfDatabase database)
	{
		// Check the version of the BDF file
		if(!"BDF3".contentEquals(new String(database.getBytes(0, 4)))) {
			lookupTable = new BdfLookupTable();
			bdf = new BdfObject(lookupTable);
			return;
		}
		
		// Get the lookup table
		int lookupTable_size = DataHelpers.getByteBuffer(database.getPointer(4, 4)).getInt();
		lookupTable = new BdfLookupTable(database.getPointer(8, lookupTable_size));
		
		// Get the rest of the data
		int upto = lookupTable_size + 8;
		int bdf_size = DataHelpers.getByteBuffer(database.getPointer(upto, 4)).getInt();
		bdf = new BdfObject(lookupTable, database.getPointer(upto + 4, bdf_size));
	}
	
	public BdfDatabase serialize()
	{
		int bdf_size = bdf.serializeSeeker();
		int lookupTable_size = lookupTable.serializeSeeker();
		int database_size = bdf_size + lookupTable_size + 12;
		BdfDatabase database = new BdfDatabase(database_size);
		
		database.setBytes(0, "BDF3".getBytes());
		database.setBytes(4, DataHelpers.serializeInt(lookupTable_size));
		database.setBytes(8 + lookupTable_size, DataHelpers.serializeInt(bdf_size));
		
		lookupTable.serialize(database.getPointer(8));
		bdf.serialize(database.getPointer(12 + lookupTable_size));
		
		return database;
	}
	
	public BdfObject getBDF() {
		return bdf;
	}
	
	public String serializeHumanReadable(BdfIndent indent) {
		return serializeHumanReadable(indent, 0);
	}
	
	public String serializeHumanReadable() {
		return serializeHumanReadable(new BdfIndent("", ""), 0);
	}
	
	public String serializeHumanReadable(BdfIndent indent, int it)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		try {
			bdf.serializeHumanReadable(stream, indent, it);
			return stream.toString();
		}
		
		catch(IOException e) {
			return "undefined";
		}
	}
	
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent) throws IOException {
		bdf.serializeHumanReadable(stream, indent, 0);
	}
	
	public void serializeHumanReadable(OutputStream stream) throws IOException {
		bdf.serializeHumanReadable(stream, new BdfIndent("", ""), 0);
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
}
