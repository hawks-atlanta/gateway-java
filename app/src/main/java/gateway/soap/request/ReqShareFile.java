package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqShareFile extends Authorization
{
	@NotNull public UUID fileUUID;
	@NotEmpty public String otherUsername;
}
