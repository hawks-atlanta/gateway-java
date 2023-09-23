package gateway.soap.request;

import java.util.UUID;

public class ReqFileMove extends Authorization
{
	public UUID fileUUID;
	public UUID targetDirectoryUUID; // always a directory
	public String newName;			 // optional
}
