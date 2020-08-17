package bdf.data;

import java.io.IOException;
import java.io.OutputStream;

public class BdfDatabasePointer implements IBdfDatabase
{
	byte[] database;
	int location;
	int size;
	
	public BdfDatabasePointer(byte[] bytes) {
		database = bytes;
		size = bytes.length;
		location = 0;
	}
	
	public BdfDatabasePointer(String str) {
		this(str.getBytes());
	}
	
	public BdfDatabasePointer(int size) {
		this.database = new byte[size];
		this.location = 0;
		this.size = size;
	}
	
	BdfDatabasePointer() {
	}
	
	@Override
	public IBdfDatabase getCopy(int start, int end)
	{
		byte[] database = new byte[end - start];
		
		for(int i=0;i<end-start;i++) {
			database[i] = this.database[i + start + location];
		}
		
		return new BdfDatabase(database);
	}
	
	@Override
	public byte getByte() {
		return database[location];
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
		BdfDatabasePointer db = new BdfDatabasePointer();
		
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
		for(int i=0;i<size;i+=BdfDatabase.STREAM_CHUNK_SIZE)
		{
			if(size - i < BdfDatabase.STREAM_CHUNK_SIZE) {
				stream.write(getBytes(i + start, size - i));
			} else {
				stream.write(getBytes(i + start, BdfDatabase.STREAM_CHUNK_SIZE));
			}
		}
	}
	
	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		writeToStream(stream, 0, size);
	}
	
	@Override
	public void setByte(int pos, byte b) {
		database[pos + location] = b;
	}
	
	@Override
	public void setByte(byte b) {
		database[location] = b;
	}
	
	@Override
	public void setBytes(IBdfDatabase bytes) {
		setBytes(bytes, 0, bytes.size());
	}
	
	@Override
	public void setBytes(IBdfDatabase bytes, int offset) {
		setBytes(bytes, offset, bytes.size());
	}
	
	@Override
	public void setBytes(IBdfDatabase bytes, int offset, int length)
	{
		for(int i=0;i<length;i++) {
			database[offset + location + i] = bytes.getByte(i);
		}
	}
	
	@Override
	public void setBytes(byte[] bytes, int offset, int length)
	{
		for(int i=0;i<length;i++) {
			database[offset + location + i] = bytes[i];
		}
	}
	
	@Override
	public void setBytes(byte[] bytes, int offset) {
		setBytes(bytes, offset, bytes.length);
	}
	
	@Override
	public void setBytes(byte[] bytes) {
		setBytes(bytes, 0, bytes.length);
	}
}
