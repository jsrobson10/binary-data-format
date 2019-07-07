package bdf.util;

import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;

public class DataHelpers
{
	public static ByteBuffer getByteBuffer(BdfDatabase db) {
		return ByteBuffer.wrap(db.getBytes());
	}
	
	public static BdfDatabase getDatabase(ByteBuffer buffer) {
		return new BdfDatabase(buffer.array());
	}
	
	public static BdfDatabase serializeInt(int value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE/8);
		buffer.putInt(value);
		return getDatabase(buffer);
	}
	
	public static boolean bytesAreEqual(byte[] b1, byte[] b2)
	{
		// Send back false if the lengths are different
		if(b1.length != b2.length) return false;
		
		// Loop over the bytes
		for(int i=0;i<b1.length;i++)
		{
			// Send back false if the bytes are different
			if(b1[i] != b2[i]) return false;
		}
		
		// Send back true if everything has been checked
		return true;
	}
}
