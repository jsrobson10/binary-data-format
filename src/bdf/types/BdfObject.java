package bdf.types;

import java.nio.ByteBuffer;

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
			if(type == BdfTypes.STRING) object = database.getString();
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
		if(type == BdfTypes.STRING) database = new BdfDatabase((String)object);
		if(type == BdfTypes.ARRAY) database = ((BdfArray)object).serialize();
		if(type == BdfTypes.NAMED_LIST) database = ((BdfNamedList)object).serialize();
		
		return BdfDatabase.add(new BdfDatabase(type), database);
	}
	
	private String calcIndent(BdfIndent indent, int it) {
		String t = "";
		t += indent.breaker;
		for(int i=0;i<=it;i++) {
			t += indent.indent;
		}
		return t;
	}
	
	public String serializeHumanReadable(BdfIndent indent, int it)
	{
		if(type == BdfTypes.BOOLEAN) {
			if(this.getBoolean()) return "true";
			else return "false";
		}
		
		// Objects
		if(type == BdfTypes.ARRAY) return ((IBdfType)object).serializeHumanReadable(indent, it);
		if(type == BdfTypes.NAMED_LIST) return ((IBdfType)object).serializeHumanReadable(indent, it);
		if(type == BdfTypes.STRING) return DataHelpers.serializeString((String)object);
		
		// Primitives
		if(type == BdfTypes.BYTE) return (Byte.toString(this.getByte())+"B");
		if(type == BdfTypes.DOUBLE) return (Double.toString(this.getDouble())+"D");
		if(type == BdfTypes.FLOAT) return (Float.toString(this.getFloat())+"F");
		if(type == BdfTypes.INTEGER) return (Integer.toString(this.getInteger())+"I");
		if(type == BdfTypes.LONG) return (Long.toString(this.getLong())+"L");
		if(type == BdfTypes.SHORT) return (Short.toString(this.getShort())+"S");
		if(type == BdfTypes.BOOLEAN) return this.getBoolean() ? "true" : "false";
		
		// Arrays
		if(type == BdfTypes.ARRAY_INTEGER) {
			String str = "(" + calcIndent(indent, it);
			for(int i : this.getIntegerArray()) {
				str += Integer.toString(i) + "I, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_BOOLEAN) {
			String str = "(" + calcIndent(indent, it);
			for(boolean i : this.getBooleanArray()) {
				str += (i ? "true" : "false") + ", " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_BYTE) {
			String str = "(" + calcIndent(indent, it);
			for(byte i : this.getByteArray()) {
				str += Byte.toString(i) + "B, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_LONG) {
			String str = "(" + calcIndent(indent, it);
			for(long i : this.getLongArray()) {
				str += Long.toString(i) + "L, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_SHORT) {
			String str = "(" + calcIndent(indent, it);
			for(short i : this.getShortArray()) {
				str += Short.toString(i) + "S, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_DOUBLE) {
			String str = "(" + calcIndent(indent, it);
			for(double i : this.getDoubleArray()) {
				str += Double.toString(i) + "D, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		if(type == BdfTypes.ARRAY_FLOAT) {
			String str = "(" + calcIndent(indent, it);
			for(float i : this.getFloatArray()) {
				str += Float.toString(i) + "F, " + calcIndent(indent, it);
			}
			str = str.substring(0, str.length() - 2) + ")";
			return str;
		}
		
		// Return null if the object is undefined
		return "undefined";
	}
	
	public BdfObject() {
		database = new BdfDatabase();
	}
	
	public byte getType() {
		return this.type;
	}
	
	// Primitives
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
	
	// Arrays
	public int[] getIntegerArray()
	{
		if(this.type == BdfTypes.ARRAY_INTEGER)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			int[] array = new int[b.capacity() / Integer.BYTES];
			
			for(int i = 0; i < b.capacity() / Integer.BYTES; i++) {
				array[i] = b.getInt();
			}
			
			object = array;
			return array;
		}
		
		else {
			return new int[0];
		}
	}
	
	public boolean[] getBooleanArray()
	{
		if(this.type == BdfTypes.ARRAY_BOOLEAN)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			boolean[] array = new boolean[b.capacity()];
			
			for(int i = 0; i < b.capacity(); i++) {
				array[i] = b.get() == 0x01 ? true : false;
			}
			
			return array;
		}
		
		else {
			return new boolean[0];
		}
	}
	
	public long[] getLongArray()
	{
		if(this.type == BdfTypes.ARRAY_LONG)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			long[] array = new long[b.capacity() / Long.BYTES];
			
			for(int i = 0; i < b.capacity() / Long.BYTES; i++) {
				array[i] = b.getLong();
			}
			
			return array;
		}
		
		else {
			return new long[0];
		}
	}
	
	public short[] getShortArray()
	{
		if(this.type == BdfTypes.ARRAY_SHORT)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			short[] array = new short[b.capacity() / Short.BYTES];
			
			for(int i = 0; i < b.capacity() / Short.BYTES; i++) {
				array[i] = b.getShort();
			}
			
			return array;
		}
		
		else {
			return new short[0];
		}
	}
	
	public byte[] getByteArray()
	{
		if(this.type == BdfTypes.ARRAY_BYTE)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			byte[] array = new byte[b.capacity() / Byte.BYTES];
			
			for(int i = 0; i < b.capacity() / Byte.BYTES; i++) {
				array[i] = b.get();
			}
			
			return array;
		}
		
		else {
			return new byte[0];
		}
	}
	
	public double[] getDoubleArray()
	{
		if(this.type == BdfTypes.ARRAY_DOUBLE)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			double[] array = new double[b.capacity() / Double.BYTES];
			
			for(int i = 0; i < b.capacity() / Double.BYTES; i++) {
				array[i] = b.getDouble();
			}
			
			return array;
		}
		
		else {
			return new double[0];
		}
	}
	
	public float[] getFloatArray()
	{
		if(this.type == BdfTypes.ARRAY_FLOAT)
		{
			ByteBuffer b = DataHelpers.getByteBuffer(database);
			float[] array = new float[b.capacity() / Float.BYTES];
			
			for(int i = 0; i < b.capacity() / Float.BYTES; i++) {
				array[i] = b.getFloat();
			}
			
			return array;
		}
		
		else {
			return new float[0];
		}
	}
	
	// Objects
	public String getString()
	{
		if(this.type != BdfTypes.STRING)
			this.setString("");
		
		return (String)object;
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
	
	public BdfObject setBooleanArray(boolean[] value) {
		this.type = BdfTypes.ARRAY_BOOLEAN;
		ByteBuffer b = ByteBuffer.allocate(value.length);
		for(boolean v : value) {
			b.put((byte) (v ? 0x01 : 0x00));
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setIntegerArray(int[] value) {
		this.type = BdfTypes.ARRAY_INTEGER;
		ByteBuffer b = ByteBuffer.allocate(value.length * Integer.BYTES);
		for(int v : value) {
			b.putInt(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setLongArray(long[] value) {
		this.type = BdfTypes.ARRAY_LONG;
		ByteBuffer b = ByteBuffer.allocate(value.length * Long.BYTES);
		for(long v : value) {
			b.putLong(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setShortArray(short[] value) {
		this.type = BdfTypes.ARRAY_SHORT;
		ByteBuffer b = ByteBuffer.allocate(value.length * Short.BYTES);
		for(short v : value) {
			b.putShort(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setByteArray(byte[] value) {
		this.type = BdfTypes.ARRAY_BYTE;
		ByteBuffer b = ByteBuffer.allocate(value.length * Byte.BYTES);
		for(byte v : value) {
			b.put(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setDoubleArray(double[] value) {
		this.type = BdfTypes.ARRAY_DOUBLE;
		ByteBuffer b = ByteBuffer.allocate(value.length * Double.BYTES);
		for(double v : value) {
			b.putDouble(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	public BdfObject setFloatArray(float[] value) {
		this.type = BdfTypes.ARRAY_FLOAT;
		ByteBuffer b = ByteBuffer.allocate(value.length * Float.BYTES);
		for(float v : value) {
			b.putFloat(v);
		}
		database = DataHelpers.getDatabase(b);
		return this;
	}
	
	// Primitives
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
	
	// Arrays
	public static BdfObject withIntegerArray(int[] v) {
		return (new BdfObject()).setIntegerArray(v);
	}
	
	public static BdfObject withByteArray(byte[] v) {
		return (new BdfObject()).setByteArray(v);
	}
	
	public static BdfObject withBooleanArray(boolean[] v) {
		return (new BdfObject()).setBooleanArray(v);
	}
	
	public static BdfObject withFloatArray(float[] v) {
		return (new BdfObject()).setFloatArray(v);
	}
	
	public static BdfObject withDoubleArray(double[] v) {
		return (new BdfObject()).setDoubleArray(v);
	}
	
	public static BdfObject withLongArray(long[] v) {
		return (new BdfObject()).setLongArray(v);
	}
	
	public static BdfObject withShortArray(short[] v) {
		return (new BdfObject()).setShortArray(v);
	}
	
	// Objects
	public static BdfObject withString(String v) {
		return (new BdfObject()).setString(v);
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
