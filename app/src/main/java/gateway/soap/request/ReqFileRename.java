package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileRename extends Authorization
{
	@NotNull public UUID fileUUID;
	@NotNull public String newName;
}
