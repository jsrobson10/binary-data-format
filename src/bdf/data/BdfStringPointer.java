package bdf.data;

import bdf.util.BdfError;

public class BdfStringPointer
{
	char[] data;
	int offset;
	
	public BdfStringPointer getPointer(int offset) {
		return new BdfStringPointer(data, this.offset + offset);
	}
	
	public void increment(int amount) {
		offset += amount;
	}
	
	public void increment() {
		offset += 1;
	}
	
	public char[] getDataCharArray() {
		return data;
	}
	
	public int getDataLocation() {
		return offset;
	}
	
	public int getDataLength() {
		return data.length;
	}
	
	public char[] getCharArray(int offset, int length)
	{
		char[] array = new char[length];
		
		for(int i=0;i<length;i++) {
			array[i] = data[i + offset + this.offset];
		}
		
		return array;
	}
	
	public char[] getCharArray(int length) {
		return getCharArray(0, length);
	}
	
	public BdfStringPointer(char[] data, int offset) {
		this.data = data;
		this.offset = offset;
	}

	public char getChar(int i) {
		return data[offset + i];
	}
	
	public char getChar() {
		return data[offset];
	}

	public void ignoreBlanks()
	{
		while(true)
		{
			if(offset >= data.length) {
				throw BdfError.createError(BdfError.ERROR_END_OF_FILE, this);
			}
			
			char c = getChar();
			
			if(!(c == '\n' || c == '\t' || c == ' ')) {
				return;
			}
			
			increment();
		}
	}

	// In the format "abc\n\t\u0003..."
	public String getQuotedString()
	{
		if(getChar() != '"') {
			throw BdfError.createError(BdfError.ERROR_SYNTAX, this);
		}
		
		increment();
		String str = "";
		
		while(true)
		{
			if(offset >= data.length) {
				throw BdfError.createError(BdfError.ERROR_END_OF_FILE, this);
			}
			
			char c = getChar();
			
			// Check for back slashes
			if(c == '\\')
			{
				increment(1);
				c = getChar();
				
				switch(c)
				{
				case 'n':
					str += "\n";
					break;
				case 't':
					str += "\t";
					break;
				case '"':
					str += "\"";
					break;
				case '\\':
					str += "\\";
					break;
				case 'u': // \u0000
				{
					if(offset + 5 >= data.length) {
						throw BdfError.createError(BdfError.ERROR_END_OF_FILE, getPointer(1));
					}
					
					char[] hex = getCharArray(1, 4);
					char unicode = (char)0;
					int m = 1;
					
					for(int j=hex.length-1;j>=0;j--)
					{
						c = hex[j];
						
						if(c >= '0' && c <= '9') {
							unicode += (char)(m * (c - '0'));
						}
						
						else if(c >= 'a' && c <= 'f') {
							unicode += (char)(m * (c - 'a' + 10));
						}
						
						else {
							throw BdfError.createError(BdfError.ERROR_SYNTAX, getPointer(1 + (hex.length-j-1)));
						}
						
						m *= 16;
					}
					
					str += unicode;
					increment(5);
					
					break;
				}
				default:
					str += "\\" + c;
				}
			}
			
			else if(c == '"') {
				increment();
				break;
			}
			
			else {
				increment();
				str += c;
			}
		}
		
		return str;
	}
	
	public boolean isNext(String check)
	{
		if(check.length() + offset >= data.length) {
			return false;
		}
		
		for(int i=0;i<check.length();i++)
		{
			if(offset + i >= data.length) {
				throw BdfError.createError(BdfError.ERROR_END_OF_FILE, this);
			}
			
			char c = getChar(i);
			c = (char)((c >= 'A' && c <= 'Z') ? (c + 32) : c);
			
			if(c != check.charAt(i)) {
				return false;
			}
		}
		
		increment(check.length());
		
		return true;
	}
	
	public boolean isInteger()
	{
		for(int i=offset;i<data.length;i++)
		{
			char c = data[i];
			
			switch(c)
			{
			case 'I':
				return true;
			case 'L':
				return true;
			case 'S':
				return true;
			case 'B':
				return true;
			case 'D':
				return false;
			case 'F':
				return false;
			case 'e':
				continue;
			case 'E':
				continue;
			case '.':
				continue;
			}
			
			if(c >= '0' && c <= '9') {
				continue;
			}
			
			throw BdfError.createError(BdfError.ERROR_SYNTAX, new BdfStringPointer(data, i));
		}
		
		throw BdfError.createError(BdfError.ERROR_END_OF_FILE, new BdfStringPointer(data, data.length - 1));
	}
}
