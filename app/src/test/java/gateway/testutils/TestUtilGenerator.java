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

	public static byte[] randomASCIIBytes (int size)
	{
		byte[] buff = new byte[size];
		Random rn = new Random ();

		for (int i = 0; i < size; i++) {
			buff[i] = (byte)(rn.nextInt () % 255);
		}

		return buff;
	}
}
