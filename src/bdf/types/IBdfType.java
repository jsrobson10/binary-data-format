package bdf.types;

import bdf.data.IBdfDatabase;

interface IBdfType
{
	int serialize(IBdfDatabase database);
	int serializeSeeker();
	
	String serializeHumanReadable(BdfIndent indent, int it);

	public default String serializeHumanReadable(BdfIndent indent) {
		return this.serializeHumanReadable(indent, 0);
	}
	
	public default String serializeHumanReadable() {
		return this.serializeHumanReadable(new BdfIndent("", ""), 0);
	}
}
