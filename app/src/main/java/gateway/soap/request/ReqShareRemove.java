package gateway.soap.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReqShareRemove extends Authorization {
	@NotNull
	public UUID fileUUID;
	@NotEmpty
	public String otherUsername;
	
	public ReqShareRemove(String token, @NotNull UUID fileUUID, @NotEmpty String otherUsername) {
		super(token);
		this.fileUUID = fileUUID;
		this.otherUsername = otherUsername;
	}
}
