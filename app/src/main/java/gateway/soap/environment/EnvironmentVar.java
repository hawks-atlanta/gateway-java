package gateway.soap.environment;

public class EnvironmentVar
{
	public static final String AUTH_BASEURL =
		System.getenv ().getOrDefault ("AUTH_BASEURL", "http://localhost:8081");
}