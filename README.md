# Binary Data Format

### Links

- <a href="#overview">Overview</a>
- <a href="#languages">Languages</a>
- <a href="#data-types">Data types</a>
- <a href="#creating-an-object">Creating an object</a>
- <a href="#arrays">Arrays</a>
- <a href="#named-lists">Named lists</a>
- <a href="#human-readable-representation">Human readable representation</a>
- <a href="#special-notes">Special notes</a>

### Overview

Binary Data Format (BDF) is a statically typed data representation
format. It was made to be free, fast, compact, and seamlessly
convertable between its human readable and binary representations.

### Languages

- <a href="https://github.com/jsrobson10/BdfCpp">C++</a>
- Java

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

// Set an integer with an automatic type
bdf.setAutoInt(53);

// Set a primitive array of ints
int intArray[] = {3, 4, 5, 6};
bdf.setIntegerArray(intArray);

// Get a byte array
byte[] byteArray = bdf.getByteArray();

// Get the type of variable of the object
int type = bdf.getType();

// Compare the type with a type from BdfTypes
if(type == BdfTypes.INTEGER)
{

}

// Serialize the BDF object
byte[] data = bdf->serialize(&data, &data_size);

// Load another reader object from the serialized bytes
BdfReader reader2 = new BdfReader(data);

/*
	A reader object can be serialized to the human readable
	representation as a string or sent over a stream
*/
reader2.serializeHumanReadable(System.out, new BdfIndent("\t", "\n"));
String data_hr = reader2.serializeHumanReadable(new BdfIndent("\t", "\n"));

// A reader object can be loaded from a human readable object
BdfReader reader3 = new BdfReaderHuman(data_hr);

```

### Arrays

Arrays can be used to store chunks of information, they hold instances of
BdfObject. Arrays can also be iterated over just like any other array.

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
array.add(bdf.newObject().setByte((byte)53));

// Set the array to the bdf object
bdf.setArray(array);

```

### Named lists

Named lists can be used to store data under ids/strings
to be used like variables in a program. A named list
can be created similar to an array.

```java

BdfReader reader = new BdfReader();
BdfObject bdf = reader.getObject();

// New named list
BdfNamedList list = bdf.newNamedList();

// Set an element to the named list
list.set("key1", bdf.newObject().setInteger(5));

// Use ids instead of strings for optimisation
// if set/get is being called multiple times
// on the same key.

int key2 = bdf.getKeyLocation("key2");
list.set(key2, bdf.newObject().setFloat(42.0F));

// Get an elements value
int v = list.get("key1").getInteger();

// Check if an element exists
bool has_key = list.contains("key1");

// Get the lists keys
int[] keys = list.getKeys();

// Iterate over the lists keys
for(int key : keys)
{
	// Get the keys name
	String key_name = bdf.getKeyName(key);
}

```

### Human readable representation

A big part of binary data format is the human readable
representation. It has a JSON-like syntax.
This can be used with config files and to modify/view
binaries. A big advantage to using the human readable
representation in configuration files is its support
for comments.

```hbdf

/*
	A Named List is represented
	by an opening tag and a closing
	tag {  }
*/
{
	/*
		A key value pair can be stored
		within a Named List with a string
		property
	*/
	"hello": "world",

	/*
		Integers can be stored here too.
		They have a character at the end
		to say what type they are.
		
		The tag at the end can be:
			- I: Integer - a value between -2^31 and 2^31 - 1
			- S: Short - a value between -32768 and 32767
			- L: Long - a value between -2^63 and 2^63 - 1
			- B: Byte - a value between -128 and 127
			- D: Double - has 15 decimal digits of precision
			- F: Float - has 7 decimal digits of precision
	*/
	"number": 42I,
	"byte": -23B,
	"decimal": 73.5D,

	/*
		This is a boolean. It can
		be true or false.
	*/
	"boolTest": false,

	/*
		Primitive arrays are represented
		by a type, an opening tag, and a
		closing tag. They are like an array
		but they contain only 1 data type.

		The tag at the start can be:
			- int
			- short
			- long
			- byte
			- double
			- float
			- bool
	*/
	"intArray": int (
		64I, 42I, 63I,
		22I, 96I, -12I,
	),

	/*
		The double and float types support
		Infinity, -Infinity, and NaN.
		
		They also support both really
		high and really low value numbers.
	*/
	"doubleArray": double (
		42.5D, -20D, 400D,
		NaND, -InfinityD, InfinityD,
		5.3e-200F, 4e+500F, 2.2e200F,
	)

	/*
		Arrays are enclosed by an opening
		tag and a closing tag [   ]
		
		Like the Named List, it can hold
		any data type.
	*/
	"people": [
		{"name": "foo", "age": 60B},
		{"name": "bar", "age": 21B},
	],

	// This is a single-line comment

	/* This is a multi-line comment */
}

```

### Special notes

Don't mix bdf types between different
readers, this will cause problems.

```java

BdfReader reader1 = new BdfReader();
BdfReader reader2 = new BdfReader();

BdfObject bdf1 = reader1.getObject();
BdfObject bdf2 = reader2.getObject();

// Don't do this
bdf1.setNamedList(bdf2.newNamedList());

// Or this
bdf1.setArray(bdf2.newArray());

// Or this
BdfNamedList nl = bdf1.newArray();
nl.set("illegal", bdf2.newObject().setString("action"));

```

