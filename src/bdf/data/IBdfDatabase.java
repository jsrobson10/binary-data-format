package bdf.data;

import java.io.IOException;
import java.io.OutputStream;

public interface IBdfDatabase
{
	public IBdfDatabase getCopy(int start, int end);
	public IBdfDatabase getPointer(int location, int size);
	public IBdfDatabase getPointer(int location);
	
	public void writeToStream(OutputStream stream, int start, int size) throws IOException;
	public void writeToStream(OutputStream stream) throws IOException;
	
	public int size();
	
	public byte[] getBytes();
	public byte[] getBytes(int start, int size);
	
	public byte getByte();
	public byte getByte(int i);
	public String getString();
	
	public void setBytes(byte[] bytes, int offset, int length);
	public void setBytes(byte[] bytes, int offset);
	public void setBytes(byte[] bytes);
	
	public void setBytes(IBdfDatabase bytes, int offset, int length);
	public void setBytes(IBdfDatabase bytes, int offset);
	public void setBytes(IBdfDatabase bytes);
	
	public void setByte(int pos, byte b);
	public void setByte(byte b);
}
