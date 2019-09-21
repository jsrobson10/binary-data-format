package bdf.types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.sun.org.apache.bcel.internal.generic.Type;

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
			if(type == BdfTypes.STRING) object = database.getBytes();
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
		if(type == BdfTypes.STRING) database = new BdfDatabase(this.getByteBuffer().array());
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
	
	public byte getType() {
		return this.type;
	}
	
	public int getInteger()
	{
		if(this.type == BdfTypes.INTEGER)
			return DataHelpers.getByteBuffer(database).getInt(0);
		else
			return 0;
	}
	
	public byte getByte()
	{
		if(this.type == BdfTypes.BYTE)
			return database.getByte(0);
		else
			return 0;
	}
	
	public boolean getBoolean()
	{
		if(this.type == BdfTypes.BOOLEAN)
			return database.getByte(0) == 0x01;
		else
			return false;
	}
	
	public double getDouble()
	{
		if(this.type == BdfTypes.DOUBLE)
			return DataHelpers.getByteBuffer(database).getDouble(0);
		else
			return 0;
	}
	
	public float getFloat()
	{
		if(this.type == BdfTypes.FLOAT)
			return DataHelpers.getByteBuffer(database).getFloat(0);
		else
			return 0;
	}
	
	public long getLong()
	{
		if(this.type == BdfTypes.LONG)
			return DataHelpers.getByteBuffer(database).getLong(0);
		else
			return 0;
	}
	
	public short getShort()
	{
		if(this.type == BdfTypes.SHORT)
			return DataHelpers.getByteBuffer(database).getShort(0);
		else
			return 0;
	}
	
	public String getString()
	{
		if(this.type != BdfTypes.STRING)
			this.setString("");
		
		return new String((byte[])object, StandardCharsets.UTF_8);
	}
	
	public ByteBuffer getByteBuffer()
	{
		if(this.type != BdfTypes.STRING)
			this.setString("");
		
		return ByteBuffer.wrap((byte[])object);
	}
	
	public BdfArray getArray()
	{
		if(this.type != BdfTypes.ARRAY)
			this.setArray();
		
		return (BdfArray)object;
	}
	
	public BdfNamedList getNamedList()
	{
		if(this.type != BdfTypes.NAMED_LIST)
			this.setNamedList();
		
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
		this.database = new BdfDatabase(value.getBytes());
		this.object = value.getBytes();
		return this;
	}
	
	public BdfObject setByteBuffer(ByteBuffer value) {
		this.type = BdfTypes.STRING;
		this.database = new BdfDatabase(value.array());
		this.object = value.array();
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
	
	public BdfObject setArray() {
		return this.setArray(new BdfArray());
	}
	
	public BdfObject setNamedList() {
		return this.setNamedList(new BdfNamedList());
	}
	
	public static BdfObject withInteger(int v) {
		return (new BdfObject()).setInteger(v);
	}
	
	public static BdfObject withByte(byte v) {
		return (new BdfObject()).setByte(v);
	}
	
	public static BdfObject withBoolean(boolean v) {
		return (new BdfObject()).setBoolean(v);
	}
	
	public static BdfObject withFloat(float v) {
		return (new BdfObject()).setFloat(v);
	}
	
	public static BdfObject withDouble(double v) {
		return (new BdfObject()).setDouble(v);
	}
	
	public static BdfObject withLong(long v) {
		return (new BdfObject()).setLong(v);
	}
	
	public static BdfObject withShort(short v) {
		return (new BdfObject()).setShort(v);
	}
	
	public static BdfObject withString(String v) {
		return (new BdfObject()).setString(v);
	}
	
	public static BdfObject withByteBuffer(ByteBuffer v) {
		return (new BdfObject()).setByteBuffer(v);
	}
	
	public static BdfObject withArray(BdfArray v) {
		return (new BdfObject()).setArray(v);
	}
	
	public static BdfObject withNamedList(BdfNamedList v) {
		return (new BdfObject()).setNamedList(v);
	}

	public static BdfObject withArray() {
		return (new BdfObject()).setArray(new BdfArray());
	}
	
	public static BdfObject withNamedList() {
		return (new BdfObject()).setNamedList(new BdfNamedList());
	}

}
