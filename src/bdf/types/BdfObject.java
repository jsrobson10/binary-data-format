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
	
	public static BdfObject withArray(BdfArray v) {
		return (new BdfObject()).setArray(v);
	}
	
	public static BdfObject withBdfNamedList(BdfNamedList v) {
		return (new BdfObject()).setNamedList(v);
	}

	public static BdfObject withArray() {
		return (new BdfObject()).setArray(new BdfArray());
	}
	
	public static BdfObject withBdfNamedList() {
		return (new BdfObject()).setNamedList(new BdfNamedList());
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
	
	public BdfObject setArray() {
		return this.setArray(new BdfArray());
	}
	
	public BdfObject setNamedList() {
		return this.setNamedList(new BdfNamedList());
	}
	
	public BdfObject set(int v) {
	  return this.setInteger(v);
	}
	
	public BdfObject set(byte v) {
	  return this.setByte(v);
	}
	
	public BdfObject set(boolean v) {
	  return this.setBoolean(v);
	}
	
	public BdfObject set(float v) {
	  return this.setFloat(v);
	}
	
	public BdfObject set(double v) {
	  return this.setDouble(v);
	}
	
	public BdfObject set(long v) {
	  return this.setLong(v);
	}
	
	public BdfObject set(short v) {
	  return this.setShort(v);
	}
	
	public BdfObject set(String v) {
	  return this.setString(v);
	}
	
	public BdfObject set(BdfArray v) {
	  return this.setArray(v);
	}
	
	public BdfObject set(BdfNamedList v) {
	  return this.setNamedList(v);
	}
	
	public BdfObject setIfInvalid(int v) {
	  if(this.getType() == BdfTypes.INTEGER) return this;
	  return this.setInteger(v);
	}

	public BdfObject setIfInvalid(byte v) {
	  if(this.getType() == BdfTypes.BYTE) return this;
	  return this.setByte(v);
	}

	public BdfObject setIfInvalid(boolean v) {
	  if(this.getType() == BdfTypes.BOOLEAN) return this;
	  return this.setBoolean(v);
	}

	public BdfObject setIfInvalid(float v) {
	  if(this.getType() == BdfTypes.FLOAT) return this;
	  return this.setFloat(v);
	}

	public BdfObject setIfInvalid(double v) {
	  if(this.getType() == BdfTypes.DOUBLE) return this;
	  return this.setDouble(v);
	}

	public BdfObject setIfInvalid(long v) {
	  if(this.getType() == BdfTypes.LONG) return this;
	  return this.setLong(v);
	}

	public BdfObject setIfInvalid(short v) {
	  if(this.getType() == BdfTypes.SHORT) return this;
	  return this.setShort(v);
	}

	public BdfObject setIfInvalid(String v) {
	  if(this.getType() == BdfTypes.STRING) return this;
	  return this.setString(v);
	}

	public BdfObject setIfInvalid(BdfArray v) {
	  if(this.getType() == BdfTypes.ARRAY) return this;
	  return this.setArray(v);
	}

	public BdfObject setIfInvalid(BdfNamedList v) {
	  if(this.getType() == BdfTypes.NAMED_LIST) return this;
	  return this.setNamedList(v);
	}

	public BdfObject setIntegerIfInvalid(int v) {
	  if(this.getType() == BdfTypes.INTEGER) return this;
	  return this.setInteger(v);
	}

	public BdfObject setByteIfInvalid(byte v) {
	  if(this.getType() == BdfTypes.BYTE) return this;
	  return this.setByte(v);
	}

	public BdfObject setBooleanIfInvalid(boolean v) {
	  if(this.getType() == BdfTypes.BOOLEAN) return this;
	  return this.setBoolean(v);
	}

	public BdfObject setFloatIfInvalid(float v) {
	  if(this.getType() == BdfTypes.FLOAT) return this;
	  return this.setFloat(v);
	}

	public BdfObject setDoubleIfInvalid(double v) {
	  if(this.getType() == BdfTypes.DOUBLE) return this;
	  return this.setDouble(v);
	}

	public BdfObject setLongIfInvalid(long v) {
	  if(this.getType() == BdfTypes.LONG) return this;
	  return this.setLong(v);
	}

	public BdfObject setShortIfInvalid(short v) {
	  if(this.getType() == BdfTypes.SHORT) return this;
	  return this.setShort(v);
	}

	public BdfObject setStringIfInvalid(String v) {
	  if(this.getType() == BdfTypes.STRING) return this;
	  return this.setString(v);
	}

	public BdfObject setArrayIfInvalid(BdfArray v) {
	  if(this.getType() == BdfTypes.ARRAY) return this;
	  return this.setArray(v);
	}

	public BdfObject setNamedListIfInvalid(BdfNamedList v) {
	  if(this.getType() == BdfTypes.NAMED_LIST) return this;
	  return this.setNamedList(v);
	}
	
	public BdfObject setArrayIfInvalid() {
	  if(this.getType() == BdfTypes.ARRAY) return this;
	  return this.setArray(new BdfArray());
	}

	public BdfObject setNamedListIfInvalid() {
	  if(this.getType() == BdfTypes.NAMED_LIST) return this;
	  return this.setNamedList(new BdfNamedList());
	}

}
