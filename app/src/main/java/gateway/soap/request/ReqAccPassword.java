package gateway.soap.request;

import jakarta.validation.constraints.NotNull;

public class ReqAccPassword extends Authorization
{
	@NotNull public String oldpassword;
	@NotNull public String newpassword;
}
