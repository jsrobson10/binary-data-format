package bdf.data;

import java.nio.charset.StandardCharsets;

public class BdfDatabase
{
	protected byte[] database = null;
	
	public BdfDatabase() {
		this.database = new byte[0];
	}
	
	public BdfDatabase(String database) {
		this.database = database.getBytes();
	}
	
	public BdfDatabase(byte ...database) {
		this.database = database;
	}
	
	public BdfDatabase getAt(int start, int end)
	{
		byte[] split = new byte[end - start];
		
		for(int i=start;i<end;i++) {
			split[i-start] = this.database[i];
		}
		
		return new BdfDatabase(split);
	}
	
	public int length() {
		return this.database.length;
	}
	
	public byte[] getBytes() {
		return this.database;
	}
	
	public byte getByte(int i) {
		return this.database[i];
	}
	
	public String getString() {
		return new String(this.database, StandardCharsets.UTF_8);
	}
	
	public static BdfDatabase add(BdfDatabase d1, BdfDatabase d2)
	{
		byte[] added = new byte[d1.length() + d2.length()];
		
		for(int i=0;i<d1.length();i++) {
			added[i] = d1.getByte(i);
		}
		
		for(int i=0;i<d2.length();i++) {
			added[d1.length()+i] = d2.getByte(i);
		}
		
		return new BdfDatabase(added);
	}
}
