package bdf.types;

import java.io.IOException;
import java.io.OutputStream;

import bdf.data.IBdfDatabase;

interface IBdfType
{
	int serialize(IBdfDatabase database);
	int serializeSeeker();
	
	void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) throws IOException;
}
