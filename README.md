# Binary Data Format

### Links

- <a href="#overview">Overview</a>
- <a href="#data-types">Data types</a>
- <a href="#creating-an-object">Creating an object</a>
- <a href="#arrays">Arrays</a>
- <a href="#named-lists">Named lists</a>
- <a href="#saving-classes">Saving classes</a>

### Overview

Binary Data Format (or BDF) is designed to store data in a tree-like binary structure,
like Notch's NBT format, but also open source and free like JSON. The format is
fast and allows multiple data types, it uses 32-bit integers, so BDF files can
be fast and work well on 32-bit systems, but have a maximum size of 2 GB.
BDF allows human readable serialization to see what is going on for debugging
purposes, but it currently can't parse the human readable serialized string to an object.

### Data types

- Boolean
- Integer
- Long
- Short
- Byte
- Double
- Float
- String
- Array
- Named List
- Empty

### Creating an object

You will need to create a new object to store any data, use a `BdfObject` instance.
You can input serialized data into `BdfObject` via a `BdfDatabase`.

```java

// New BDF object
BdfObject bdf = new BdfObject();

// Get an integer
int v = bdf.getInteger();

// Set an integer
bdf.setInteger(5);

// Get the type of variable of the object
int type = bdf.getType();

// Compare the type with a type from BdfTypes
if(type == BdfTypes.INTEGER)
{

}

// Serialize the BDF object
byte[] data = bdf.serialize().getBytes();



// Load another BDF object with the serialized bytes
BdfObject bdf2 = new BdfObject(new BdfDatabase(data));

```

A `BdfFileManager`
instance can be used in the same way as a `BdfObject`, but it also needs a String parameter
for the path of the file. The file can be written with `BdfFileManager.saveDatabase()`.
A `BdfFileManager` is an instance of `BdfObject`, a `BdfFileManager` can be casted to
a `BdfObject`.

```java

// Open a file
BdfFileManager bdf = new BdfFileManager("file.bdf");

// Save the database
bdf.saveDatabase();

// The file can be casted to a BdfObject
BdfObject bdf2 = (BdfObject) bdf;

// Bdf
System.out.println(bdf instanceof BdfObject); // true

```

### Arrays

Arrays can be used to store lists of information, they hold `BdfObject`.
The array is called with `new BdfArray()`. It can hold information, get
the size of the array with `BdfArray.size()`, remove elements with
`BdfArray.remove(index)`, set indexes with `BdfArray.set(index, BdfObject)`,
and add elements with `BdfArray.add(BdfObject)`. Arrays also
have support for Iterators and are an instance of `Iterable`.

```java

// New BDF Object
BdfObject bdf = new BdfObject();

// New BDF Array
BdfArray array = new BdfArray();

// Size
int size = array.size();

// Remove
array.remove(3); // Could be any number

// Set - Could be any number with any object
array.set(4, BdfObject.withString("A String"));

// Add - Could be any object
array.add(BdfObject.withByte(53));

// Set the array to the bdf object
bdf.setArray(array);

// Iterate over an array
for(BdfObject o : array)
{

}

```

### Named lists

Named lists can be used to store data under strings,
to be used like variables in a program. A named list
can be created with `new BdfNamedList()` and it
has the ability to set with `BdfNamedList.set(String, BdfObject)`,
remove with `BdfNamedList.remove(String)`, and check
for a key with `BdfNamedList.contains(String)`. It also has
features to get all the keys with `BdfNamedList.getKeys()`.
Named lists also have Iterator support and are an instance of
`Iterable`.

```java

// New bdf named list
BdfNamedList list = new BdfNamedList();

// Set an element with a value
list.set("key1", BdfObject.withInteger(5));

// Get an elements value
int v = list.get("key1").getInteger();

// Check if an element exists
boolean has_key = list.contains("key1");

// Get the lists keys
String[] keys = list.getKeys();

// Iterate over the lists keys
for(String key : keys)
{

}

```

### Saving classes

Classes can be saved with `BdfClassManager` and by
implementing the `IBdfClassManager` interface,
adding 2 functions `BdfClassLoad` and `BdfClassSave`.
`BdfClassLoad` is for checking and loading data from
bdf into the classes variables, while `BdfClassSave`
is for packing pre-existing variables into bdf format.
A BdfClassManager can be used to pass the `IBdfClassManager`
interface into.

A class with `IBdfClassManager` to save the data
could look like this:

```java

class HelloWorld implements IBdfClassManager
{
	int iterator = 0;

	@Override
	public void BdfClassLoad(BdfObject bdf)
	{
		// Load scripts here
		
		// Get the named list
		BdfNamedList nl = bdf.getNamedList();
		
		// Set the iterator stored in bdf
		int iterator = nl.get("iterator").getInteger();
	}
	
	@Override
	public void BdfClassSave(BdfObject bdf)
	{
		// Save scripts here
		
		// Create a named list
		BdfNamedList nl = new BdfNamedList();
		
		// Set the iterator to the named list
		nl.set("iterator", BdfObject.withInteger(iterator));
		
		// Store the named list
		bdf.setNamedList(nl);
	}
	
	public void hello()
	{
		// Increase the iterator by 1
		iterator++;
		
		// Say "Hello, World! Script executed <iterator> times!"
		System.out.println("Hello, World! Script executed "+iterator+" times!");
	}

}

```

A script to manage this could look something like this:

```java

/*
	Get a new BdfObject instance, it could be existing,
	or from another file, BdfArray, or BdfNamedList instance.
*/
BdfObject bdf = new BdfObject();

// Create the HelloWorld class
HelloWorld hello = new HelloWorld();

// Get a new BdfClassManager instance to deal with BDF data
BdfClassManager manager = new BdfClassManager(hello);

// Give the manager an existing BdfObject instance
manager.setBdf(bdf);

// Load the classes bdf data
manager.load();

// Call the hello world function
hello.hello();

// Save the classes bdf data
manager.save();

```