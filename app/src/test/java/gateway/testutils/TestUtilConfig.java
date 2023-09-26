package gateway.testutils;

import gateway.config.Config;

public class TestUtilConfig
{
	public static void makeInvalidAll ()
	{
		Config.setMetadataBaseUrl ("");
		Config.setAuthBaseUrl ("");
		Config.setWorkerHost ("");
		Config.setWorkerPort (0);
	}

	public static void makeInvalidMetadata () { Config.setMetadataBaseUrl (""); }

	public static void makeInvalidWorker ()
	{
		Config.setWorkerHost ("");
		Config.setWorkerPort (1);
	}
}
