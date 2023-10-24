package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileDelete extends Authorization
{
	@NotNull public UUID fileUUID;
}
