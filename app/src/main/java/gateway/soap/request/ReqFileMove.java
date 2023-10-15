package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileMove extends Authorization
{
	@NotNull public UUID fileUUID;
	public UUID targetDirectoryUUID; // always a directory
}
