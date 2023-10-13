package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqShareRemove
{
	@NotNull public UUID fileUUID;
	@NotEmpty public String otherUsername;
}
