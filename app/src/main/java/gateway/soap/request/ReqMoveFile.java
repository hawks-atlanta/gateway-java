package gateway.soap.request;

import java.util.UUID;

public class ReqMoveFile extends Operation
{
	public UUID nameUUID;
	public UUID targetLocationUUID; // always a directory
	public String newName;			// optional
}
