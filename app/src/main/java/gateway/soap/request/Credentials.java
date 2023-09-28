package gateway.soap.request;

public class Credentials
{
	public String username;
	public String password;

	public Credentials () {}
	public Credentials (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
}
