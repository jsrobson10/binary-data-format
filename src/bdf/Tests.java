package bdf;

import bdf.data.BdfDatabase;
import bdf.types.BdfArray;
import bdf.types.BdfNamedList;
import bdf.types.BdfObject;

public class Tests {

	public static void main(String[] args)
	{
		// Create a new BdfObject instance
		BdfObject bdf = new BdfObject();

		// Create a named list
		BdfNamedList bdf_nl = new BdfNamedList();

		// Add some variables to the named list
		bdf_nl.set("boolean", BdfObject.with(true));
		bdf_nl.set("an_int", BdfObject.with((int)53));
		bdf_nl.set("double", new BdfObject().setDouble(632.5));

		// Output some checks on BdfNamedList
		System.out.println(bdf_nl.contains("an_int")); // true
		System.out.println(bdf_nl.contains("this_dosn't_exist")); // false

		// Create an array
		BdfArray bdf_array = new BdfArray();

		// Add some values to the array
		bdf_array.add(BdfObject.with("Hello, World!"));
		bdf_array.add(BdfObject.with(1234567890L));
		bdf_array.set(1, BdfObject.with((short)432));
		
		// Output the size of the array
		System.out.println(bdf_array.size()); // 2
		
		// Output the type of the 2nd item in the array, value types are in BdfTypes
		System.out.println(bdf_array.get(1).getType()); // 3 (BdfTypes.SHORT)
		
		// Save the array to the named list
		bdf_nl.set("array", BdfObject.with(bdf_array));
		
		// Set the named list to the bdf object
		bdf.setNamedList(bdf_nl);
		
		// Serialize the data
		byte[] bdf_data = bdf.serialize().getBytes();
		
		
		
		// Load the serialized data
		BdfObject bdf2 = new BdfObject(new BdfDatabase(bdf_data));
		
		// Show the human readable serialized data
		System.out.println(bdf2.serializeHumanReadable()); // {"boolean": true, "an_int": 53I, "double": 632.5D, "array": ["Hello, World!", 432S]}
		
		// Show the value of the boolean in the named list
		System.out.println(bdf2.getNamedList().get("boolean").getBoolean()); // true
		
		// Show the value of item 0 in the array
		System.out.println(bdf2.getNamedList().get("array").getArray().get(0).getString());	// Hello, World!
		
		// Check if the double exists
		System.out.println(bdf2.getNamedList().contains("double")); // true
		
		// Remove the double from the named list
		bdf2.getNamedList().remove("double");
		
		// Check if the double exists
		System.out.println(bdf2.getNamedList().contains("double")); // false
		
	}

}
