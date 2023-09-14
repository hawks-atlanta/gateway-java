package gateway.config;

public class Config
{
	private static String authBaseUrl;
	private static String metadataBaseUrl;
	private static String workerHost;
	private static int workerPort;

	public static String getAuthBaseUrl () { return authBaseUrl; }
	public static String getMetadataBaseUrl () { return metadataBaseUrl; }
	public static String getWorkerHost () { return workerHost; }
	public static int getWorkerPort () { return workerPort; }

	public static void initializeFromEnv ()
	{
		authBaseUrl = System.getenv ().getOrDefault ("AUTHENTICATION_BASEURL", "127.0.0.1");
		metadataBaseUrl = System.getenv ().getOrDefault ("METADATA_BASEURL", "127.0.0.1");
		workerHost = System.getenv ().getOrDefault ("WORKER_HOST", "127.0.0.1");
		workerPort = Integer.parseInt (System.getenv ().getOrDefault ("WORKER_PORT", "1099"));
	}
}
