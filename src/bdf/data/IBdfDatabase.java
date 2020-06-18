package bdf.data;

import java.io.IOException;
import java.io.OutputStream;

public interface IBdfDatabase
{
	public IBdfDatabase getAt(int start, int end);
	public IBdfDatabase getPointer(int location, int size);
	public IBdfDatabase getPointer(int location);
	
	public void writeToStream(OutputStream stream, int start, int size) throws IOException;
	public void writeToStream(OutputStream stream) throws IOException;
	
	public int size();
	
	public byte[] getBytes();
	public byte[] getBytes(int start, int size);
	
	public byte getByte(int i);
	public String getString();
	
	public void setBytes(int pos, byte[] bytes);
	public void setByte(int pos, byte b);
}
