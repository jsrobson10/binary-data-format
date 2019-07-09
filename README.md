# Binary Data Format

### Links

- <a href="#overview">Overview</a>
- <a href="#data-types">Data Types</a>
- <a href="#creating-an-object">Creating an object</a>
- <a href="#arrays">Arrays</a>
- <a href="#named-lists">Named lists</a>
- <a href="#example-bdf-program">Example BDF program</a>

### Overview

Binary Data Format (or BDF) is designed to store data in a tag-like binary structure,
like Notch's NBT format, but also open source and free like JSON. The format is
fast and allows multiple data types, it uses 32-bit integers, so BDF files can
be fast and work well on 32-bit systems, but have a maximum size of 2 GB.
BDF allows human readable serialization to see what is going on for debugging
purposes, but it currently can't parse the result to an object.

### Data Types

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
array.set(4, BdfObject.with("A String"));

// Add - Could be any object
array.add(BdfObject.with(53));

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
list.set("key1", BdfObject.with(5));

// Get an elements value
int v = list.get("key1").getInteger();

// Check if an element exists
boolean has_key = list.contains("key1");

// Get the lists keys
String[] keys = list.getKeys();

// Iterate over the lists keys
for(String key : list)
{

}

```


### Example BDF program

```java

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
		
```