package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReqAccPassword extends Authorization
{
	@NotNull public String oldpassword;
	@NotEmpty public String newpassword;
}
