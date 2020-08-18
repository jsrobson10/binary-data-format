package bdf.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import bdf.data.BdfDatabase;
import bdf.data.BdfStringPointer;
import bdf.data.IBdfDatabase;
import bdf.util.BdfError;
import bdf.util.DataHelpers;
import tests.Tests;

public class BdfObject implements IBdfType
{
	protected IBdfDatabase database = null;
	protected Object object = null;
	protected byte type = BdfTypes.UNDEFINED;
	protected BdfLookupTable lookupTable;
	protected int last_seek;
	
	BdfObject(BdfLookupTable lookupTable) {
		this.lookupTable = lookupTable;
		this.database = new BdfDatabase(0);
	}
	
	BdfObject(BdfLookupTable lookupTable, IBdfDatabase data)
	{
		this.lookupTable = lookupTable;
		
		// Get the type and database values
		int flags = 0xff & data.getByte(0);
		type = (byte)(flags % 18);
		flags = (byte)((flags - type) / 18);
		int size_bytes = getSizeBytes(flags % 3);
		
		database = data.getPointer(1);
		
		// Skip the size bytes if size is stored
		if(shouldStoreSize(type)) {
			database = database.getPointer(size_bytes);
		}
		
		// Set the object variable if there is an object specified
		if(type == BdfTypes.STRING) object = database.getString();
		if(type == BdfTypes.ARRAY) object = new BdfArray(lookupTable, database);
		if(type == BdfTypes.NAMED_LIST) object = new BdfNamedList(lookupTable, database);
		
		if(object != null) {
			database = null;
		}
	}
	
	BdfObject(BdfLookupTable lookupTable, BdfStringPointer ptr)
	{
		this.lookupTable = lookupTable;
		
		char c = ptr.getChar();
		
		if(c == '{') {
			setNamedList(new BdfNamedList(lookupTable, ptr));
			return;
		}
		
		if(c == '[') {
			setArray(new BdfArray(lookupTable, ptr));
			return;
		}
		
		if(c == '"') {
			setString(ptr.getQuotedString());
			return;
		}
		
		boolean isPrimitiveArray = false;
		byte type = 0;
		
		if(ptr.isNext("int")) {
			type = BdfTypes.ARRAY_INTEGER;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("long")) {
			type = BdfTypes.ARRAY_LONG;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("byte")) {
			type = BdfTypes.ARRAY_BYTE;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("short")) {
			type = BdfTypes.ARRAY_SHORT;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("bool")) {
			type = BdfTypes.ARRAY_BOOLEAN;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("double")) {
			type = BdfTypes.ARRAY_DOUBLE;
			isPrimitiveArray = true;
		}
		
		else if(ptr.isNext("float")) {
			type = BdfTypes.ARRAY_FLOAT;
			isPrimitiveArray = true;
		}
		
		// Deserialize a primitive array
		if(isPrimitiveArray)
		{
			ptr.ignoreBlanks();
			
			if(ptr.getChar() != '(') {
				throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
			}
			
			ptr.increment();
			ptr.ignoreBlanks();
			
			// Get the size of the array
			int size = 0;
			
			// Get a copy of the pointer
			BdfStringPointer ptr2 = ptr.getPointer(0);
			
			for(;;)
			{
				if(ptr2.isNext("true") || ptr2.isNext("false")) {
					size += 1;
				}
				
				else
				{
					for(;;)
					{
						c = ptr2.getChar();
						
						if(c >= '0' && c <= '9' || c == '.' || c == 'e' || c == 'E' || c == '-') {
							ptr2.increment();
							continue;
						}
						
						if(c == 'B' || c == 'S' || c == 'I' || c == 'L' || c == 'D' || c == 'F') {
							ptr2.increment();
							size += 1;
							break;
						}
						
						throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr2);
					}
				}
				
				ptr2.ignoreBlanks();
				
				if(ptr2.getChar() == ',') {
					ptr2.increment();
					ptr2.ignoreBlanks();
				}
				
				if(ptr2.getChar() == ')') {
					ptr2.increment();
					break;
				}
			}
			
			Object array = null;
			
			switch(type)
			{
			case BdfTypes.ARRAY_BOOLEAN:
				array = new boolean[size];
				break;
			case BdfTypes.ARRAY_BYTE:
				array = new byte[size];
				break;
			case BdfTypes.ARRAY_DOUBLE:
				array = new double[size];
				break;
			case BdfTypes.ARRAY_FLOAT:
				array = new float[size];
				break;
			case BdfTypes.ARRAY_INTEGER:
				array = new int[size];
				break;
			case BdfTypes.ARRAY_LONG:
				array = new long[size];
				break;
			case BdfTypes.ARRAY_SHORT:
				array = new short[size];
				break;
			}
			
			for(int i=0;;i++)
			{
				if(ptr.isNext("true"))
				{
					if(type != BdfTypes.ARRAY_BOOLEAN) {
						throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
					}
					
					boolean[] a = (boolean[]) array;
					a[i] = true;
				}
				
				else if(ptr.isNext("false"))
				{
					if(type != BdfTypes.ARRAY_BOOLEAN) {
						throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
					}
					
					boolean[] a = (boolean[]) array;
					a[i] = false;
				}
				
				else
				{
					// Parse a number
					String number = "";
					
					for(;;)
					{
						c = ptr.getChar();
						
						if(ptr.getDataLocation() > ptr.getDataLength()) {
							throw BdfError.createError(BdfError.ERROR_END_OF_FILE, ptr);
						}
						
						if(c >= '0' && c <= '9' || c == '.' || c == 'e' || c == 'E' || c == '-') {
							ptr.increment();
							number += c;
							continue;
						}
						
						switch(c)
						{
							case 'D':
							{
								if(type != BdfTypes.ARRAY_DOUBLE)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								double[] a = (double[]) array;
								a[i] = Double.parseDouble(number);
								
								ptr.increment();
								break;
							}
							
							case 'F': 
							{
								if(type != BdfTypes.ARRAY_FLOAT)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								float[] a = (float[]) array;
								a[i] = Float.parseFloat(number);
								
								ptr.increment();
								break;
							}
							
							case 'I': 
							{
								if(type != BdfTypes.ARRAY_INTEGER)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								int[] a = (int[]) array;
								a[i] = Integer.parseInt(number);
								
								ptr.increment();
								break;
							}
							
							case 'L': 
							{
								if(type != BdfTypes.ARRAY_LONG)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								long[] a = (long[]) array;
								a[i] = Long.parseLong(number);
								
								ptr.increment();
								break;
							}
							
							case 'S': 
							{
								if(type != BdfTypes.ARRAY_SHORT)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								short[] a = (short[]) array;
								a[i] = Short.parseShort(number);
								
								ptr.increment();
								break;
							}
							
							case 'B': 
							{
								if(type != BdfTypes.ARRAY_BYTE)
									throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
								
								byte[] a = (byte[]) array;
								a[i] = Byte.parseByte(number);
								
								ptr.increment();
								break;
							}
							
							default:
								throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
						}
						
						break;
					}
				}
				
				// int (420I, 23I  )
				
				ptr.ignoreBlanks();
				
				if(ptr.getChar() == ',') {
					ptr.increment();
					ptr.ignoreBlanks();
				}
				
				if(ptr.getChar() == ')') {
					ptr.increment();
					break;
				}
			}
			
			switch(type)
			{
			case BdfTypes.ARRAY_BOOLEAN:
				setBooleanArray((boolean[])array);
				break;
			case BdfTypes.ARRAY_BYTE:
				setByteArray((byte[])array);
				break;
			case BdfTypes.ARRAY_DOUBLE:
				setDoubleArray((double[])array);
				break;
			case BdfTypes.ARRAY_FLOAT:
				setFloatArray((float[])array);
				break;
			case BdfTypes.ARRAY_INTEGER:
				setIntegerArray((int[])array);
				break;
			case BdfTypes.ARRAY_LONG:
				setLongArray((long[])array);
				break;
			case BdfTypes.ARRAY_SHORT:
				setShortArray((short[])array);
				break;
			}
			
			return;
		}
		
		if(ptr.isNext("true")) {
			setBoolean(true);
			return;
		}
		
		if(ptr.isNext("false")) {
			setBoolean(false);
			return;
		}
		
		if(ptr.isNext("undefined")) {
			return;
		}
		
		// Parse a number
		String number = "";
		
		for(;;)
		{
			c = ptr.getChar();
			ptr.increment();
			
			if(ptr.getDataLocation() > ptr.getDataLength()) {
				throw BdfError.createError(BdfError.ERROR_END_OF_FILE, ptr);
			}
			
			if(c >= '0' && c <= '9' || c == '.' || c == 'e' || c == 'E' || c == '-') {
				number += c;
				continue;
			}
			
			switch(c)
			{
			case 'D':
				setDouble(Double.parseDouble(number));
				return;
			case 'F':
				setFloat(Float.parseFloat(number));
				return;
			case 'B':
				setByte(Byte.parseByte(number));
				return;
			case 'S':
				setShort(Short.parseShort(number));
				return;
			case 'I':
				setInteger(Integer.parseInt(number));
				return;
			case 'L':
				setLong(Long.parseLong(number));
				return;
			}
			
			throw BdfError.createError(BdfError.ERROR_SYNTAX, ptr);
		}
	}
	
	public byte getType() {
		return type;
	}
	
	private boolean shouldStoreSize(byte b) {
		return b > 7;
	}
	
	static private int getSizeBytes(int size_bytes_tag)
	{
		switch(size_bytes_tag)
		{
		case 0: return 4;
		case 1: return 2;
		case 2: return 1;
		default: return 4;
		}
	}
	
	static byte getParentFlags(IBdfDatabase db)
	{
		int flags = 0xff & db.getByte(0);
		
		byte type = (byte)(flags % 18);
		flags = (byte)((flags - type) / 18);
		
		byte size_bytes = (byte)(flags % 3);
		flags = (byte)((flags - size_bytes) / 3);
		
		byte parent_flags = (byte)(flags % 3);
		flags = (byte)((flags - parent_flags) / 3);
		
		return parent_flags;
	}
	
	static int getSize(IBdfDatabase db)
	{
		int flags = 0xff & db.getByte(0);
		byte type = (byte)(flags % 18);
		flags = (byte)((flags - type) / 18);
		
		int size_bytes = getSizeBytes(flags % 3);
		int size = getSize(type);
		
		if(size != -1) {
			return size;
		}
		
		ByteBuffer size_buff = DataHelpers.getByteBuffer(db.getPointer(1, size_bytes));
		
		switch(size_bytes)
		{
		case 4: return size_buff.getInt();
		case 2: return (0xffff & size_buff.getShort());
		case 1: return (0xff & size_buff.get());
		}
		
		return 0;
	}
	
	static int getSize(byte type)
	{
		switch(type)
		{
		case BdfTypes.BOOLEAN:
			return 2;
		case BdfTypes.BYTE:
			return 2;
		case BdfTypes.DOUBLE:
			return 9;
		case BdfTypes.FLOAT:
			return 5;
		case BdfTypes.INTEGER:
			return 5;
		case BdfTypes.LONG:
			return 9;
		case BdfTypes.SHORT:
			return 3;
		case BdfTypes.UNDEFINED:
			return 1;
		default:
			return -1;
		}
	}
	
	@Override
	public void getLocationUses(int[] locations)
	{
		if(type == BdfTypes.NAMED_LIST || type == BdfTypes.ARRAY) {
			((IBdfType)object).getLocationUses(locations);
		}
	}
	
	@Override
	public int serialize(IBdfDatabase database, int[] locations, byte parent_flags)
	{
		int size = last_seek;
		boolean storeSize = shouldStoreSize(type);
		
		byte size_bytes_tag = 0;
		int size_bytes = 0;
		
		if(storeSize)
		{
			if(size > 65535) {		// >= 2 ^ 16
				size_bytes_tag = 0;
				size_bytes = 4;
			} else if(size > 255) {	// >= 2 ^ 8
				size_bytes_tag = 1;
				size_bytes = 2;
			} else {				// < 2 ^ 8
				size_bytes_tag = 2;
				size_bytes = 1;
			}
		}
		
		int offset = size_bytes + 1;
		byte flags = (byte)(type + (size_bytes_tag * 18) + (parent_flags * 3 * 18));
		
		// Objects
		switch(type)
		{
		case BdfTypes.ARRAY:
			size = ((BdfArray)object).serialize(database.getPointer(offset), locations, (byte)0) + offset;
			break;
			
		case BdfTypes.NAMED_LIST:
			size = ((BdfNamedList)object).serialize(database.getPointer(offset), locations, (byte)0) + offset;
			break;
			
		case BdfTypes.STRING:
			byte[] str = ((String)object).getBytes();
			size = str.length + offset;
			database.setBytes(str, offset);
			break;
			
		default:
			size = this.database.size() + offset;
			database.setBytes(this.database, offset);
			break;
		}
		
		database.setByte(0, flags);
		
		if(storeSize)
		{
			byte[] bytes = DataHelpers.serializeInt(size);
			
			for(int i=0;i<size_bytes;i++) {
				database.setByte(i + 1, bytes[i - size_bytes + 4]);
			}
		}
		
		return size;
	}

	@Override
	public int serializeSeeker(int[] locations)
	{
		int size = getSize(type);
		
		if(size != -1) {
			last_seek = size;
			return size;
		}
		
		// Objects
		switch(type)
		{
		case BdfTypes.ARRAY:
			size = ((BdfArray)object).serializeSeeker(locations) + 1;
			break;
		case BdfTypes.NAMED_LIST:
			size = ((BdfNamedList)object).serializeSeeker(locations) + 1;
			break;
		case BdfTypes.STRING:
			size = ((String)object).getBytes().length + 1;
			break;
		default:
			size = database.size() + 1;
		}
		
		int size_bytes;
		
		if(size > 65531) {			// >= 2 ^ 16
			size_bytes = 4;
		} else if(size > 253) {		// >= 2 ^ 8
			size_bytes = 2;
		} else {					// < 2 ^ 8
			size_bytes = 1;
		}
		
		size += size_bytes;
		last_seek = size;
		
		return size;
	}
	
	private String calcIndent(BdfIndent indent, int it) {
		String t = "";
		for(int i=0;i<=it;i++) {
			t += indent.indent;
		}
		return t;
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
			stream.write(("int(" + calcIndent(indent, it)).getBytes());
			int[] array = this.getIntegerArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Integer.toString(array[i]) + "I").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_BOOLEAN: {
			stream.write(("bool(" + calcIndent(indent, it)).getBytes());
			boolean[] array = this.getBooleanArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + (array[i] ? "true" : "false")).getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_SHORT: {
			stream.write(("short(" + calcIndent(indent, it)).getBytes());
			short[] array = this.getShortArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Short.toString(array[i]) + "S").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
			
		case BdfTypes.ARRAY_LONG: {
			stream.write(("long(" + calcIndent(indent, it)).getBytes());
			long[] array = this.getLongArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Long.toString(array[i]) + "L").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_BYTE: {
			stream.write(("byte(" + calcIndent(indent, it)).getBytes());
			byte[] array = this.getByteArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Byte.toString(array[i]) + "B").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_DOUBLE: {
			stream.write(("double(" + calcIndent(indent, it)).getBytes());
			double[] array = this.getDoubleArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Double.toString(array[i]) + "D").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
			}
			stream.write((indent.breaker + calcIndent(indent, it - 1) + ")").getBytes());
			break;
		}
		
		case BdfTypes.ARRAY_FLOAT: {
			stream.write(("float(" + calcIndent(indent, it)).getBytes());
			float[] array = this.getFloatArray();
			for(int i=0;i<array.length;i++) {
				stream.write((indent.breaker + calcIndent(indent, it) + Float.toString(array[i]) + "F").getBytes());
				if(i != array.length - 1) stream.write(", ".getBytes());
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
	
	public BdfObject setAutoInt(long number)
	{
		if(number > 2147483648L || number <= -2147483648L) {
			setLong(number);
		} else if(number > 32768 || number <= -32768) {
			setInteger((int)number);
		} else if(number > 128 || number <= -128) {
			setShort((short)number);
		} else {
			setByte((byte)number);
		}
		
		return this;
	}
	
	public long getAutoInt()
	{
		switch(type)
		{
		case BdfTypes.BYTE:
			return getByte();
		case BdfTypes.SHORT:
			return getShort();
		case BdfTypes.INTEGER:
			return getInteger();
		case BdfTypes.LONG:
			return getLong();
		default:
			return 0;
		}
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
			setArray(newArray());
		
		return (BdfArray)object;
	}
	
	public BdfNamedList getNamedList()
	{
		if(this.type != BdfTypes.NAMED_LIST)
			setNamedList(newNamedList());
		
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
		database = new BdfDatabase(new byte[] {value});
		return this;
	}
	
	public BdfObject setBoolean(boolean value) {
		this.type = BdfTypes.BOOLEAN;
		if(value) database = new BdfDatabase(new byte[] {0x01});
		else database = new BdfDatabase(new byte[] {0x00});
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
	
	public BdfObject newObject() {
		return new BdfObject(lookupTable);
	}
	
	public BdfNamedList newNamedList() {
		return new BdfNamedList(lookupTable);
	}
	
	public BdfArray newArray() {
		return new BdfArray(lookupTable, 0);
	}
	
	public BdfArray newArray(int size) {
		return new BdfArray(lookupTable, size);
	}
	
	public int getKeyLocation(String key) {
		return lookupTable.getLocation(key.getBytes());
	}
	
	public String getKeyName(int key) {
		return new String(lookupTable.getName(key));
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

}
