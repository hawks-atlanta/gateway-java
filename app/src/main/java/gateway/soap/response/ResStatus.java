package gateway.soap.response;

public class ResStatus
{
	public int code = 500;
	public boolean error = true;
	public String msg = "";

	public static <T extends ResStatus> T downCast (Class<T> c, ResStatus superclass)
	{
		try {
			T subclass = c.getDeclaredConstructor ().newInstance ();
			subclass.code = superclass.code;
			subclass.error = superclass.error;
			subclass.msg = superclass.msg;
			return subclass;
		} catch (Exception e) {
			throw new RuntimeException (e);
		}
	}
}
