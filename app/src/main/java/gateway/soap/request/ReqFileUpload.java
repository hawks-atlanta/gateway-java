package gateway.soap.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqFileUpload extends Authorization
{
	@NotNull public String fileName;

	@NotNull public byte[] fileContent;

	public UUID location;
}
