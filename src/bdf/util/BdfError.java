package bdf.util;

import bdf.data.BdfStringPointer;

public class BdfError extends RuntimeException
{
	private static final long serialVersionUID = -8731016151496842808L;
	private static final String[] ERRORS = {
			"Syntax error",
			"End of file",
			"Unescaped comment",
			"Unescaped string",
			"Number format error",
	};
	
	public static final int ERROR_SYNTAX = 0;
	public static final int ERROR_END_OF_FILE = 1;
	public static final int ERROR_UNESCAPED_COMMENT = 2;
	public static final int ERROR_UNESCAPED_STRING = 3;
	public static final int ERROR_NUMBER = 4;
	
	public static BdfError createError(int errorID, BdfStringPointer ptr)
	{
		String error = ERRORS[errorID];
		
		char[] array = ptr.getDataCharArray();
		int location = ptr.getDataLocation();
		
		int start_of_line = 0;
		int line = 0;
		int at = 0;
		
		for(int i=0;i<location;i++)
		{
			if(array[i] == '\n') {
				start_of_line = i + 1;
				line += 1;
				at = 0;
				continue;
			}
			
			at += 1;
		}
		
		int line_size = 0;
		String spacer = "";
		
		for(int i=start_of_line;i<array.length;i++)
		{
			if(array[i] == '\n') {
				break;
			}
			
			line_size += 1;
			
			if(i == array.length - 1) {
				break;
			}
			
			if(i < location)
			{
				if(array[i] == '\t') {
					spacer += "\t";
					continue;
				}
				
				spacer += " ";
			}
		}
		
		char[] line_chars = new char[line_size];
		
		for(int i=0;i<line_size;i++) {
			line_chars[i] = array[start_of_line + i];
		}
		
		String message = "";
		String error_short = error + " " + (line + 1) + ":" + (at + 1);
		
		message += error_short + "\n";
		message += new String(line_chars) + "\n";
		
		message += spacer;
		message += "^";
		
		BdfError bdf_error = new BdfError(message);
		
		bdf_error.error_short = error_short;
		bdf_error.type = errorID;
		
		return bdf_error;
	}
	
	private String error_short;
	private int type;
	
	public int getType() {
		return type;
	}
	
	public String getErrorShort() {
		return error_short;
	}

	private BdfError(String message) {
		super(message);
	}
}
