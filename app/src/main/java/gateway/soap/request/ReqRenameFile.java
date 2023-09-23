package gateway.soap.request;

import java.util.UUID;

public class ReqRenameFile extends Authorization
{
	public UUID nameUUID;
	public UUID targetLocationUUID; // always a directory
	public String newName;			// optional
}
