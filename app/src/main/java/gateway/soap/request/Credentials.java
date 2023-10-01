package gateway.soap.request;

import jakarta.validation.constraints.NotNull;

public class Credentials
{
	@NotNull public String username;
	@NotNull public String password;

	public Credentials () {}
	public Credentials (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
}
