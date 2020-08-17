# Binary Data Format

### Links

- <a href="#overview">Overview</a>
- <a href="#languages">Languages</a>
- <a href="#data-types">Data types</a>
- <a href="#creating-an-object">Creating an object</a>
- <a href="#arrays">Arrays</a>
- <a href="#named-lists">Named lists</a>
- <a href="#saving-classes">Saving classes</a>
- <a href="#implementation-details">Implementation details</a>

### Overview

Binary Data Format (or BDF) is designed to store data in a tree-like binary structure,
like Notch's NBT format, but also open source and free like JSON. The format is
fast and allows multiple data types. It uses 32-bit integers, so BDF files can
be fast and work well on 32-bit systems, but have a maximum size of 2 GB.
BDF allows human readable serialization to see what is going on for debugging
purposes, but it currently can't parse the human readable serialized string to an object.


### Languages

- Java
- <a href="https://github.com/jsrobson10/BdfCpp">C++</a>

### Data types

- Undefined
- Boolean
- Integer
- Long
- Short
- Byte
- Double
- Float
- Boolean Array
- Integer Array
- Long Array
- Short Array
- Byte Array
- Double Array
- Float Array
- String
- Array
- Named List

### Creating an object

You will need to generate a BdfObject to serialize anything,
this can be done by first generating a BdfReader, or generating
a new object via an existing BdfObject.

```java

// Create a reader object
BdfReader reader = new BdfReader();

// Get the BdfObject instance
BdfObject bdf = reader.getObject();

// Generate another BdfObject instance
BdfObject bdf_new = bdf.newObject();

// Get an integer
int v = bdf.getInteger();

// Set an integer
bdf.setInteger(5);

// Set a "smart" integer
bdf.setSmartInteger(53);

// Get the type of variable of the object
int type = bdf.getType();

// Compare the type with a type from BdfTypes
if(type == BdfTypes.INTEGER)
{

}

// Serialize the BDF object
IBdfDatabase data = bdf.serialize();

// Load another BDF object with the serialized bytes
BdfObject bdf2 = new BdfObject(new BdfDatabase(data));

```

A file manager instance can be used in the same way as a reader object,
but it also needs a String parameter for the path of the file. The file
manager instance also has the capacity to use compression (by default this
uses the GZIP compression algorithm).

```java

// Open a file with compression enabled
BdfFileManager reader = new BdfFileManager("file.bdf", true);

// Save the database
reader.saveDatabase();

// The file can be casted to a BdfReader
BdfReader reader2 = (BdfReader) reader;

// Can be used just as any reader instance
BdfObject bdf = reader.getObject();

```

### Arrays

Arrays can be used to store lists of information, they hold instances of
BdfObject. Arrays have support for Iterators and are an instance of Iterable.

```java

BdfReader reader = new BdfReader();
BdfObject bdf = reader.getObject();

// Can be created from a bdf object
BdfArray array = bdf.newArray();

// Get the length of an array
int size = array.size();

// Remove any index from an array
array.remove(3);

// Set an object to an index of an array
array.set(4, bdf.newObject().setString("A String"));

// Add an object to an array
array.add(bdf.newObject().setByte(53));

// Set the array to the bdf object
bdf.setArray(array);

// Iterate over an array
for(BdfObject o : array)
{

}

```

### Named lists

Named lists can be used to store data under ids/strings
to be used like variables in a program. A named list
can be created similar to an array.

```java

BdfReader reader = new BdfReader();
BdfObject bdf = new BdfObject();

// New named list
BdfNamedList nl = bdf.newNamedList();

// Set an element to the named list
nl.set("key1", bdf.newObject().setInteger(5));

// Use ids instead of strings for optimisation
// if set/get is being called multiple times
// on the same key.

int key2 = nl.getKeyLocation("key2");
nl.set(key2, bdf.newObject().setFloat(42.0F));

// Get an elements value
int v = list.get("key1").getInteger();

// Check if an element exists
boolean has_key = list.contains("key1");

// Get the lists keys
int[] keys = list.getKeys();

// Iterate over the lists keys
for(int key : keys)
{
	// Get the keys name
	String key_name = nl.getKeyName(key);
}

```

### Further optimisations


### Implementation details

All integer data types are in the Big Endian layout.

**Flags (1 unsigned byte)**
This holds 3 values:
- Type (0-17)
- Size type (0-2)
- Parent payload (0-2)

**Type**
```
0:  UNDEFINED   (0 bytes)

1:  BOOLEAN     (1 byte, 0x00 or 0x01)
2:  INTEGER     (4 bytes)
3:  LONG        (8 bytes)
4:  SHORT       (2 bytes)
5:  BYTE        (1 byte)
6:  DOUBLE      (8 bytes)
7:  FLOAT       (4 bytes)

8:  STRING
9:  ARRAY
10:  NAMED_LIST

11: ARRAY_BOOLEAN
12: ARRAY_INTEGER
13: ARRAY_LONG
14: ARRAY_SHORT
15: ARRAY_BYTE
16: ARRAY_DOUBLE
17: ARRAY_FLOAT

```

**Size Type**
This value holds info for how big the size of
the size of the payload is, in bytes. The purpose
of this is to reduce the size as much as possible
by throwing out unneccicary zeros.

**Object**
- Flags (unsigned byte, 1 byte)
- Size (variable length)
- Payload (Any type, variable length)

**NamedList**
- Key ID (variable length)
- Payload (Object, variable length)

**Array**
- Payload (Object, variable length)
