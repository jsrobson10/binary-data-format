package bdf.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.data.IBdfDatabase;
import bdf.util.DataHelpers;

public class BdfObject implements IBdfType
{
	protected IBdfDatabase database = null;
	protected Object object = null;
	protected byte type = BdfTypes.EMPTY;
	
	public static BdfObject getNew() {
		return new BdfObject();
	}
	
	public BdfObject(byte[] data) {
		this(new BdfDatabase(data));
	}
	
	public BdfObject(IBdfDatabase data)
	{
		// Is the database length greater than 1
		if(data.size() > 1)
		{
			// Get the type and database values
			type = data.getByte(0);
			database = data.getPointer(1, data.size() - 1);
			
			// Set the object variable if there is an object specified
			if(type == BdfTypes.STRING) object = database.getString();
			if(type == BdfTypes.ARRAY) object = new BdfArray(database);
			if(type == BdfTypes.NAMED_LIST) object = new BdfNamedList(database);
			
			if(object != null) {
				database = null;
			}
		}
		
		else
		{
			// Create a new database
			database = new BdfDatabase(0);
		}
	}
	
	@Override
	public int serialize(IBdfDatabase database)
	{
		int size;
		
		IBdfDatabase db = database.getPointer(1);
		
		// Objects
		switch(type)
		{
		case BdfTypes.ARRAY:
			size = ((BdfArray)object).serialize(db) + 1;
			break;
			
		case BdfTypes.NAMED_LIST:
			size = ((BdfNamedList)object).serialize(db) + 1;
			break;
			
		case BdfTypes.STRING:
			String str = (String)object;
			size = str.length() + 1;
			db.setBytes(0, str.getBytes());
			break;
			
		default:
			size = this.database.size() + 1;
			db.setBytes(0, this.database.getBytes());
			break;
		}
		
		database.setByte(0, type);
		
		return size;
	}

	@Override
	public int serializeSeeker()
	{
		// Objects
		switch(type)
		{
		case BdfTypes.ARRAY: return ((BdfArray)object).serializeSeeker() + 1;
		case BdfTypes.NAMED_LIST: return ((BdfNamedList)object).serializeSeeker() + 1;
		case BdfTypes.STRING: return ((String)object).length() + 1;
		}
		
		// Anything else
		return database.size() + 1;
	}
	
	public BdfDatabase serialize()
	{
		BdfDatabase database = new BdfDatabase(serializeSeeker());
		
		serialize(database);
		
		return database;
	}
	
	private String calcIndent(BdfIndent indent, int it) {
		String t = "";
		for(int i=0;i<=it;i++) {
			t += indent.indent;
		}
		return t;
	}
	
	public String serializeHumanReadable(BdfIndent indent) {
		return serializeHumanReadable(indent, 0);
	}
	
	public String serializeHumanReadable() {
		return serializeHumanReadable(new BdfIndent("", ""), 0);
	}
	
	public String serializeHumanReadable(BdfIndent indent, int it)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		try {
			serializeHumanReadable(stream, indent, it);
			return stream.toString();
		}
		
		catch(IOException e) {
			return "undefined";
		}
	}
	
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent) throws IOException {
		serializeHumanReadable(stream, indent, 0);
	}
	
	public void serializeHumanReadable(OutputStream stream) throws IOException {
		serializeHumanReadable(stream, new BdfIndent("", ""), 0);
	}
	
	public void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) throws IOException
	{
		String str = null;
		
		switch(type)
		{
		case BdfTypes.ARRAY:
			((IBdfType)object).serializeHumanReadable(stream, indent, it);
			return;
			
		case BdfTypes.NAMED_LIST:
			((IBdfType)object).serializeHumanReadable(stream, indent, it);
			return;
			
		case BdfTypes.STRING:
			str = DataHelpers.serializeString((String)object);
			break;
			
		case BdfTypes.BOOLEAN:
			if(this.getBoolean()) str = "true";
			else str = "false";
			break;
			
		case BdfTypes.BYTE:
			str = Byte.toString(this.getByte())+"B";
			break;
			
		case BdfTypes.INTEGER:
			str = Integer.toString(this.getInteger())+"I";
			break;
			
		case BdfTypes.SHORT:
			str = Short.toString(this.getShort())+"S";
			break;
			
		case BdfTypes.LONG:
			str = Long.toString(this.getLong())+"L";
			break;
			
		case BdfTypes.DOUBLE:
			str = Double.toString(this.getDouble())+"D";
			break;
			
		case BdfTypes.FLOAT:
			str = Float.toString(this.getFloat())+"F";
			break;
			
		case BdfTypes.ARRAY_INTEGER: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			int[] array = this.getIntegerArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Integer.toString(array[i]) + "I").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_BOOLEAN: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			boolean[] array = this.getBooleanArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + (array[i] ? "true" : "false")).getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_SHORT: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			short[] array = this.getShortArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Short.toString(array[i]) + "S").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_LONG: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			long[] array = this.getLongArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Long.toString(array[i]) + "L").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_BYTE: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			byte[] array = this.getByteArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Byte.toString(array[i]) + "B").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_DOUBLE: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			double[] array = this.getDoubleArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Double.toString(array[i]) + "D").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_FLOAT: {
			stream.write(("(" + calcIndent(indent, it)).getBytes());
			float[] array = this.getFloatArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Float.toString(array[i]) + "F").getBytes());
				if(i == array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		default:
			str = "undefined";
			break;
		}
		
		if(str != null) {
			stream.write(str.getBytes());
		}
	}
	
	public BdfObject() {
		database = new BdfDatabase(0);
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
		ByteBuffer b = ByteBuffer.allocate(Integer.BYTES);
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
