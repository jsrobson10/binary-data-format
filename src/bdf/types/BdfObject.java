package bdf.types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import bdf.data.BdfDatabase;
import bdf.util.DataHelpers;

public class BdfObject implements IBdfType
{
	protected BdfDatabase database = null;
	protected Object object = null;
	protected byte type = BdfTypes.EMPTY;
	
	public static BdfObject getNew() {
		return new BdfObject();
	}
	
	public BdfObject(BdfDatabase data)
	{
		// Is the database length greater than 1
		if(data.length() > 1)
		{
			// Get the type and database values
			type = data.getAt(0, 1).getByte(0);
			database = data.getAt(1, data.length());
			
			// Set the object variable if there is an object specified
			if(type == BdfTypes.STRING) object = new String(database.getBytes(), StandardCharsets.UTF_8);
			if(type == BdfTypes.ARRAY) object = new BdfArray(database);
			if(type == BdfTypes.NAMED_LIST) object = new BdfNamedList(database);
		}
		
		else
		{
			// Create a new database
			database = new BdfDatabase();
		}
	}
	
	@Override
	public BdfDatabase serialize()
	{
		if(type == BdfTypes.STRING) database = new BdfDatabase(this.getString());
		if(type == BdfTypes.ARRAY) database = ((BdfArray)object).serialize();
		if(type == BdfTypes.NAMED_LIST) database = ((BdfNamedList)object).serialize();
		
		return BdfDatabase.add(new BdfDatabase(type), database);
	}
	
	public String serializeHumanReadable()
	{
		if(type == BdfTypes.BOOLEAN) {
			if(this.getBoolean()) return "true";
			else return "false";
		}
		
		if(type == BdfTypes.ARRAY) return ((IBdfType)object).serializeHumanReadable();
		if(type == BdfTypes.NAMED_LIST) return ((IBdfType)object).serializeHumanReadable();
		if(type == BdfTypes.BYTE) return (Byte.toString(this.getByte())+"B");
		if(type == BdfTypes.DOUBLE) return (Double.toString(this.getDouble())+"D");
		if(type == BdfTypes.FLOAT) return (Float.toString(this.getFloat())+"F");
		if(type == BdfTypes.INTEGER) return (Integer.toString(this.getInteger())+"I");
		if(type == BdfTypes.LONG) return (Long.toString(this.getLong())+"L");
		if(type == BdfTypes.SHORT) return (Short.toString(this.getShort())+"S");
		if(type == BdfTypes.STRING) return DataHelpers.serializeString((String)object);
		
		// Return null if the object is undefined
		return "undefined";
	}
	
	public BdfObject() {
		database = new BdfDatabase();
	}
	
	public static BdfObject with(int v) {
		return (new BdfObject()).setInteger(v);
	}
	
	public static BdfObject with(byte v) {
		return (new BdfObject()).setByte(v);
	}
	
	public static BdfObject with(boolean v) {
		return (new BdfObject()).setBoolean(v);
	}
	
	public static BdfObject with(float v) {
		return (new BdfObject()).setFloat(v);
	}
	
	public static BdfObject with(double v) {
		return (new BdfObject()).setDouble(v);
	}
	
	public static BdfObject with(long v) {
		return (new BdfObject()).setLong(v);
	}
	
	public static BdfObject with(short v) {
		return (new BdfObject()).setShort(v);
	}
	
	public static BdfObject with(String v) {
		return (new BdfObject()).setString(v);
	}
	
	public static BdfObject with(BdfArray v) {
		return (new BdfObject()).setArray(v);
	}
	
	public static BdfObject with(BdfNamedList v) {
		return (new BdfObject()).setNamedList(v);
	}
	
	public byte getType() {
		return this.type;
	}
	
	public int getInteger() {
		return DataHelpers.getByteBuffer(database).getInt(0);
	}
	
	public byte getByte() {
		return database.getByte(0);
	}
	
	public boolean getBoolean() {
		return database.getByte(0) == 0x01;
	}
	
	public double getDouble() {
		return DataHelpers.getByteBuffer(database).getDouble(0);
	}
	
	public float getFloat() {
		return DataHelpers.getByteBuffer(database).getFloat(0);
	}
	
	public long getLong() {
		return DataHelpers.getByteBuffer(database).getLong(0);
	}
	
	public short getShort() {
		return DataHelpers.getByteBuffer(database).getShort(0);
	}
	
	public String getString() {
		return (String)object;
	}
	
	public BdfArray getArray() {
		return (BdfArray)object;
	}
	
	public BdfNamedList getNamedList() {
		return (BdfNamedList)object;
	}
	
	public BdfObject setInteger(int value) {
		this.type = BdfTypes.INTEGER;
		ByteBuffer b = ByteBuffer.allocate(Integer.SIZE/8);
		b.putInt(0, value);
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setByte(byte value) {
		this.type = BdfTypes.BYTE;
		database = new BdfDatabase(value);
		return this;
	}
	
	public BdfObject setBoolean(boolean value) {
		this.type = BdfTypes.BOOLEAN;
		if(value) database = new BdfDatabase((byte)0x01);
		else database = new BdfDatabase((byte)0x00);
		return this;
	}
	
	public BdfObject setDouble(double value) {
		this.type = BdfTypes.DOUBLE;
		ByteBuffer b = ByteBuffer.allocate(Double.SIZE/8);
		b.putDouble(0, value);
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setFloat(float value) {
		this.type = BdfTypes.FLOAT;
		ByteBuffer b = ByteBuffer.allocate(Float.SIZE/8);
		b.putFloat(0, value);
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setLong(long value) {
		this.type = BdfTypes.LONG;
		ByteBuffer b = ByteBuffer.allocate(Long.SIZE/8);
		b.putLong(0, value);
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setShort(short value) {
		this.type = BdfTypes.SHORT;
		ByteBuffer b = ByteBuffer.allocate(Short.SIZE/8);
		b.putShort(0, value);
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setString(String value) {
		this.type = BdfTypes.STRING;
		this.database = new BdfDatabase(value);
		this.object = value;
		return this;
	}
	
	public BdfObject setArray(BdfArray value) {
		this.type = BdfTypes.ARRAY;
		this.object = value;
		return this;
	}
	
	public BdfObject setNamedList(BdfNamedList value) {
		this.type = BdfTypes.NAMED_LIST;
		this.object = value;
		return this;
	}
}
