package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileNewDir extends Authorization
{
	@NotNull public String directoryName;
	public UUID location;
}
