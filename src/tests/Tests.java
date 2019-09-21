package tests;

import java.nio.ByteBuffer;
import java.util.Iterator;

import bdf.classes.BdfClassManager;
import bdf.data.BdfDatabase;
import bdf.file.BdfFileManager;
import bdf.types.BdfArray;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		BdfObject bdf = new BdfObject();
		
		ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES*20);
	
		for(int i=0;i<bb.capacity()/Integer.BYTES;i+=1) {
			System.out.println("WRITE ("+i+"): "+i*10);
			bb.putInt(i*Integer.BYTES, i*10);
		}
		
		bdf.setByteBuffer(bb);
		BdfObject bdf2 = new BdfObject(new BdfDatabase(bdf.serialize().getBytes()));
		
		ByteBuffer bb2 = bdf2.getByteBuffer();
		
		/*for(int i=0;i<bb.capacity()/Integer.BYTES;i+=1) {
			System.out.println("READ ("+i+"): "+bb.getInt(i*Integer.BYTES));
		}*/
		
		for(int i=0;i<bb2.capacity()/Integer.BYTES;i+=1) {
			System.out.println("READ ("+i+"): "+bb2.getInt(i*Integer.BYTES));
		}
	}

}
