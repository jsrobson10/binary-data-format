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
	
	private void initNew() {
		lookupTable = new BdfLookupTable(this);
		bdf = new BdfObject(lookupTable);
	}
	
	public BdfReader() {
		initNew();
	}
	
	public BdfReader(byte[] database) {
		this(new BdfDatabase(database));
	}
	
	public BdfReader(IBdfDatabase database)
	{
		if(database.size() < 4) {
			initNew();
			return;
		}
		
		// Get the lookup table
		int lookupTable_size = DataHelpers.getByteBuffer(database.getPointer(0, 4)).getInt();
		lookupTable = new BdfLookupTable(this, database.getPointer(4, lookupTable_size));
		
		// Get the rest of the data
		int upto = lookupTable_size + 4;
		int bdf_size = DataHelpers.getByteBuffer(database.getPointer(upto, 4)).getInt();
		bdf = new BdfObject(lookupTable, database.getPointer(upto + 4, bdf_size));
	}
	
	public BdfDatabase serialize()
	{
		int[] locations = lookupTable.serializeGetLocations();
		
		int bdf_size = bdf.serializeSeeker(locations);
		int lookupTable_size = lookupTable.serializeSeeker(locations);
		int database_size = bdf_size + lookupTable_size + 8;
		BdfDatabase database = new BdfDatabase(database_size);
		
		database.setBytes(0, DataHelpers.serializeInt(lookupTable_size));
		database.setBytes(4 + lookupTable_size, DataHelpers.serializeInt(bdf_size));
		
		lookupTable.serialize(database.getPointer(4, lookupTable_size), locations);
		bdf.serialize(database.getPointer(8 + lookupTable_size, database_size), locations);
		
		return database;
	}
	
	public BdfObject getObject() {
		return bdf;
	}
	
	public BdfObject resetObject() {
		bdf = new BdfObject(lookupTable);
		return bdf;
	}
	
	public String serializeHumanReadable(BdfIndent indent)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		try {
			bdf.serializeHumanReadable(stream, indent, 0);
			return stream.toString();
		}
		
		catch(IOException e) {
			return "undefined";
		}
	}
	
	public String serializeHumanReadable() {
		return serializeHumanReadable(new BdfIndent("", ""));
	}
	
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent) throws IOException
	{
		bdf.serializeHumanReadable(stream, indent, 0);
		
		stream.write('\n');
		stream.flush();
	}
	
	public void serializeHumanReadable(OutputStream stream) throws IOException {
		serializeHumanReadable(stream, new BdfIndent("", ""));
	}
}
