package gateway.soap.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class ReqFileMove extends Authorization {
	@NotNull
	public UUID fileUUID;
	public UUID targetDirectoryUUID; // always a directory
	public String newName; // optional
}
