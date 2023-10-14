package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;

public class Credentials
{
	@NotEmpty public String username;
	@NotEmpty public String password;

	public Credentials () {}
	public Credentials (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
}
