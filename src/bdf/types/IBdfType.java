package bdf.types;

import java.io.IOException;
import java.io.OutputStream;

import bdf.data.IBdfDatabase;

interface IBdfType
{
	void getLocationUses(int[] locations);
	int serialize(IBdfDatabase database, int[] locations, byte flags);
	int serializeSeeker(int[] locations);
	
	void serializeHumanReadable(OutputStream stream, BdfIndent indent, int it) throws IOException;
}
