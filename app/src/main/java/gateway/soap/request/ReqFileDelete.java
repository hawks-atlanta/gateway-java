package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;

public class ReqFileDelete extends Authorization
{
	@NotEmpty public UUID[] fileUUID; // 1+
}
