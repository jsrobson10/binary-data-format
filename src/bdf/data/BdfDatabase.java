package bdf.data;

import java.io.IOException;
import java.io.OutputStream;

public class BdfDatabase implements IBdfDatabase
{
	static final int STREAM_CHUNK_SIZE = 1024*1024;
	
	byte[] database;
	
	public BdfDatabase(byte[] bytes) {
		database = bytes;
	}
	
	public BdfDatabase(String str) {
		this(str.getBytes());
	}
	
	public BdfDatabase(int size) {
		this.database = new byte[size];
	}
	
	@Override
	public IBdfDatabase getCopy(int start, int end)
	{
		byte[] database = new byte[end - start];
		
		for(int i=0;i<end-start;i++) {
			database[i] = this.database[i + start];
		}
		
		return new BdfDatabase(database);
	}
	
	@Override
	public byte getByte() {
		return database[0];
	}
	
	@Override
	public byte getByte(int i) {
		return database[i];
	}
	
	@Override
	public byte[] getBytes(int start, int size)
	{
		byte[] database = new byte[size];
		
		for(int i=0;i<size;i++) {
			database[i] = this.database[i + start];
		}
		
		return database;
	}
	
	@Override
	public byte[] getBytes() {
		return getBytes(0, database.length);
	}
	
	@Override
	public IBdfDatabase getPointer(int location, int size)
	{
		BdfDatabasePointer db = new BdfDatabasePointer();
		
		db.database = database;
		db.location = location;
		db.size = size;
		
		return db;
	}
	
	@Override
	public IBdfDatabase getPointer(int location) {
		return getPointer(location, location);
	}
	
	@Override
	public int size() {
		return database.length;
	}
	
	@Override
	public String getString() {
		return new String(database);
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
		writeToStream(stream, 0, database.length);
	}
	
	public static IBdfDatabase add(IBdfDatabase b1, IBdfDatabase b2)
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
		database[pos] = b;
	}
	
	@Override
	public void setByte(byte b) {
		database[0] = b;
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
			database[offset + i] = bytes.getByte(i);
		}
	}
	
	@Override
	public void setBytes(byte[] bytes, int offset, int length)
	{
		for(int i=0;i<length;i++) {
			database[offset + i] = bytes[i];
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
