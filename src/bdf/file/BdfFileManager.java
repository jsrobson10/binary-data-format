package bdf.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import bdf.data.BdfDatabase;
import bdf.types.BdfObject;
import bdf.util.FileHelpers;

public class BdfFileManager extends BdfObject
{
	protected String path;
	
	private static BdfDatabase init(String path)
	{
		// Get the file
		File file = new File(path);
		
		// Does the file have read access
		if(file.canRead())
		{
			// Return the files contents as a database
			return new BdfDatabase(FileHelpers.readAllIgnoreErrors(path));
		}
		
		// Return an empty database if there is no read access
		return new BdfDatabase();
	}
	
	public BdfFileManager(String path) {
		super(init(path));
		this.path = path;
	}
	
	public void saveDatabase(String path)
	{
		try
		{
			// Get the file handler
			File file = new File(path);
			
			// Create the parent directories
			file.getAbsoluteFile().getParentFile().mkdirs();
			
			// Get the database file for output
			FileOutputStream out = new FileOutputStream(path);
			
			// Write the database to the file
			out.write(this.serialize().getBytes());
			
			// Close the file output stream
			out.close();
		}
		
		catch(IOException e) {
			return;
		}
	}
	
	public void saveDatabase() {
		this.saveDatabase(this.path);
	}
}
