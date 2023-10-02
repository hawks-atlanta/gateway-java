package gateway.soap.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class ReqShareFile extends Authorization {
	@NotNull
	public UUID fileUUID;
	@NotNull
	public String otherUsername;
}
