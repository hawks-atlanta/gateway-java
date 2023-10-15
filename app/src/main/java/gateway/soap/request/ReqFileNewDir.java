package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;

public class ReqFileNewDir extends Authorization
{
	@NotEmpty public String directoryName;
	public UUID location;
}
