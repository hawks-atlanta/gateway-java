package gateway.testutils;

import java.util.Random;

public class TestUtilGenerator
{
	public static byte[] randomBytes (int size)
	{
		byte[] buff = new byte[size];
		(new Random ()).nextBytes (buff);
		return buff;
	}
}
