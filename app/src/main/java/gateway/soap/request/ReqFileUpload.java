package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileUpload extends Authorization
{
	@NotEmpty public String fileName;
	@NotNull public byte[] fileContent;
	public UUID location;
}
