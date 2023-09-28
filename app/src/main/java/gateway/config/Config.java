package gateway.config;

public class Config
{
	// const

	public static final int MAX_FILE_SIZE = 100000000; // 100 MB

	private static String authBaseUrl;
	private static String metadataBaseUrl;
	private static String workerHost;
	private static int workerPort;

	public static String getAuthBaseUrl () { return authBaseUrl; }
	public static String getMetadataBaseUrl () { return metadataBaseUrl; }
	public static String getWorkerHost () { return workerHost; }
	public static int getWorkerPort () { return workerPort; }

	public static void setAuthBaseUrl (String v) { authBaseUrl = v; }
	public static void setMetadataBaseUrl (String v) { metadataBaseUrl = v; }
	public static void setWorkerHost (String v) { workerHost = v; }
	public static void setWorkerPort (int v) { workerPort = v; }

	public static void initializeFromEnv ()
	{
		authBaseUrl =
			System.getenv ().getOrDefault ("AUTHENTICATION_BASEURL", "http://127.0.0.1:8081");
		metadataBaseUrl =
			System.getenv ().getOrDefault ("METADATA_BASEURL", "http://127.0.0.1:8082/api/v1");
		workerHost = System.getenv ().getOrDefault ("WORKER_HOST", "127.0.0.1");
		workerPort = Integer.parseInt (System.getenv ().getOrDefault ("WORKER_PORT", "1099"));
	}
}
