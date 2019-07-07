package bdf.file;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import bdf.data.BdfDatabase;
import bdf.types.BdfObject;
import bdf.util.FileHelpers;

public class BdfFileManager extends BdfObject
{
	protected String path;
	
	public BdfFileManager(String path) {
		super(new BdfDatabase(FileHelpers.readAllIgnoreErrors(path)));
		this.path = path;
	}
	
	public void saveDatabase(String path)
	{
		try
		{
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
