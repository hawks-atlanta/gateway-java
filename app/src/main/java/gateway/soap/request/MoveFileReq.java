package gateway.soap.request;

import java.util.UUID;

public class MoveFileReq extends Operation
{
	public UUID nameUUID;
	public UUID targetLocationUUID; // always a directory
	// TODO: Add nullable flag
	public String newName; // optional
}
