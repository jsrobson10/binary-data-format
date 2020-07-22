package bdf.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import bdf.data.BdfDatabase;
import bdf.types.BdfObject;
import bdf.util.FileHelpers;

public class BdfFileManager extends BdfObject
{
	protected String path;
	private boolean compressed;
	
	private static BdfDatabase init(String path, boolean compressed)
	{
		this.compressed = compressed;
		
		// Get the file
		File file = new File(path);
		
		// Does the file have read access
		if(file.canRead())
		{
			if(compressed)
			{
				// Return the files contents as a database
				return new BdfDatabase(FileHelpers.readAllCompressedIgnoreErrors(path));
			}
			
			else
			{
				// Return the files contents as a database
				return new BdfDatabase(FileHelpers.readAllIgnoreErrors(path));
			}
		}
		
		// Return an empty database if there is no read access
		return new BdfDatabase(0);
	}
	
	public BdfFileManager(String path, boolean compressed) {
		super(init(path, compressed));
		this.path = path;
	}
	
	public BdfFileManager(String path) {
		this(path, false);
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
			OutputStream out = new FileOutputStream(path);
			
			if(compressed) {
				out = new DeflaterOutputStream(out);
			}
			
			// Write the database to the file
			BdfDatabase db = this.serialize();
			db.writeToStream(out);
			
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
