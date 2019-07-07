package bdf.exception;

public class UndefinedKeyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public UndefinedKeyException(String key)
	{
		super("The key \""+key+"\" has not been defined.");
	}
}
