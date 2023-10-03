package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFile extends Authorization
{
	@NotNull public UUID fileUUID;
}
