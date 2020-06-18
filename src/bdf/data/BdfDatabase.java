package bdf.data;

import java.io.IOException;
import java.io.OutputStream;

public class BdfDatabase implements IBdfDatabase
{
	private static final int STREAM_CHUNK_SIZE = 1024*1024;
	
	private byte[] database;
	private int location;
	private int size;
	
	public BdfDatabase(byte[] bytes) {
		database = bytes;
		size = bytes.length;
		location = 0;
	}
	
	public BdfDatabase(String str) {
		this(str.getBytes());
	}
	
	public BdfDatabase(int size) {
		this.database = new byte[size];
		this.location = 0;
		this.size = size;
	}
	
	private BdfDatabase() {
	}
	
	@Override
	public IBdfDatabase getAt(int start, int end)
	{
		byte[] database = new byte[end - start];
		
		for(int i=0;i<end-start;i++) {
			database[i] = this.database[i + start + location];
		}
		
		return new BdfDatabase(database);
	}
	
	@Override
	public byte getByte(int i) {
		return database[location + i];
	}
	
	@Override
	public byte[] getBytes(int start, int size)
	{
		byte[] database = new byte[size];
		
		for(int i=0;i<size;i++) {
			database[i] = this.database[i + location + start];
		}
		
		return database;
	}
	
	@Override
	public byte[] getBytes() {
		return getBytes(0, size);
	}
	
	@Override
	public IBdfDatabase getPointer(int location, int size)
	{
		BdfDatabase db = new BdfDatabase();
		
		db.database = database;
		db.location = this.location + location;
		db.size = size;
		
		return db;
	}
	
	@Override
	public IBdfDatabase getPointer(int location) {
		return getPointer(location, size - location);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public String getString() {
		return new String(database, location, size);
	}
	
	@Override
	public void writeToStream(OutputStream stream, int start, int size) throws IOException
	{
		for(int i=0;i<size;i+=STREAM_CHUNK_SIZE)
		{
			if(size - i < STREAM_CHUNK_SIZE) {
				stream.write(getBytes(i + start, size - i));
			} else {
				stream.write(getBytes(i + start, STREAM_CHUNK_SIZE));
			}
		}
	}
	
	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		writeToStream(stream, 0, size);
	}
	
	public static BdfDatabase add(IBdfDatabase b1, IBdfDatabase b2)
	{
		byte[] bytes = new byte[b1.size() + b2.size()];
		int b1_size = b1.size();
		
		for(int i=0;i<bytes.length;i++)
		{
			if(i >= b1_size) {
				bytes[i] = b2.getByte(i - b1_size);
			} else {
				bytes[i] = b1.getByte(i);
			}
		}
		
		return new BdfDatabase(bytes);
	}
	
	@Override
	public void setByte(int pos, byte b) {
		database[pos + location] = b;
	}
	
	@Override
	public void setBytes(int pos, byte[] bytes)
	{
		for(int i=0;i<bytes.length;i++) {
			database[pos + location + i] = bytes[i];
		}
	}
}
