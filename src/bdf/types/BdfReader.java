package bdf.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.data.BdfStringPointer;
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
		if(database.size() == 0) {
			initNew();
			return;
		}
		
		int upto = 0;
		
		IBdfDatabase flag_ptr = database.getPointer(upto);
		byte lookupTable_size_tag = BdfObject.getParentFlags(flag_ptr);
		byte lookupTable_size_bytes = 0;
		
		switch(lookupTable_size_tag)
		{
		case 0:
			lookupTable_size_bytes = 4;
			break;
		case 1:
			lookupTable_size_bytes = 2;
			break;
		case 2:
			lookupTable_size_bytes = 1;
			break;
		}
		
		// Get the rest of the data
		int bdf_size = BdfObject.getSize(flag_ptr);
		IBdfDatabase database_bdf = database.getPointer(upto, bdf_size);
		upto += bdf_size;
		
		// Get the lookup table
		ByteBuffer lookupTable_size_buff = DataHelpers.getByteBuffer(database.getPointer(upto, lookupTable_size_bytes));
		int lookupTable_size = 0;
		
		switch(lookupTable_size_tag)
		{
		case 0:
			lookupTable_size = lookupTable_size_buff.getInt();
			break;
		case 1:
			lookupTable_size = 0xffff & lookupTable_size_buff.getShort();
			break;
		case 2:
			lookupTable_size = 0xff & lookupTable_size_buff.get();
			break;
		}
		
		lookupTable = new BdfLookupTable(this, database.getPointer(lookupTable_size_bytes + upto, lookupTable_size));
		bdf = new BdfObject(lookupTable, database_bdf);
	}
	
	public static BdfReader readHumanReadable(String data)
	{
		BdfReader reader = new BdfReader();
		reader.bdf = new BdfObject(reader.lookupTable, new BdfStringPointer(data.toCharArray(), 0));
		
		return reader;
	}
	
	public BdfDatabase serialize()
	{
		int[] locations = lookupTable.serializeGetLocations();
		
		int bdf_size = bdf.serializeSeeker(locations);
		int lookupTable_size = lookupTable.serializeSeeker(locations);
		
		int lookupTable_size_bytes = 0;
		byte lookupTable_size_tag = 0;
		
		if(lookupTable_size > 65535) {		// >= 2 ^ 16
			lookupTable_size_tag = 0;
			lookupTable_size_bytes = 4;
		} else if(lookupTable_size > 255) {	// >= 2 ^ 8
			lookupTable_size_tag = 1;
			lookupTable_size_bytes = 2;
		} else {							// < 2 ^ 8
			lookupTable_size_tag = 2;
			lookupTable_size_bytes = 1;
		}
		
		int upto = 0;
		int database_size = bdf_size + lookupTable_size + lookupTable_size_bytes;
		BdfDatabase database = new BdfDatabase(database_size);
		
		bdf.serialize(database.getPointer(upto, bdf_size), locations, lookupTable_size_tag);
		upto += bdf_size;
		
		byte[] bytes = DataHelpers.serializeInt(lookupTable_size);
		
		for(int i=0;i<lookupTable_size_bytes;i++) {
			database.setByte(i + upto, bytes[i - lookupTable_size_bytes + 4]);
		}
		
		lookupTable.serialize(database.getPointer(upto + lookupTable_size_bytes, lookupTable_size), locations, (byte)0);
		
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
