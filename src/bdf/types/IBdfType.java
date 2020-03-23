package bdf.types;

import bdf.data.BdfDatabase;

public interface IBdfType
{
	public BdfDatabase serialize();
	String serializeHumanReadable(BdfIndent indent, int it);

	public default String serializeHumanReadable(BdfIndent indent) {
		return this.serializeHumanReadable(indent, 0);
	}
	
	public default String serializeHumanReadable() {
		return this.serializeHumanReadable(new BdfIndent("", ""), 0);
	}
}
