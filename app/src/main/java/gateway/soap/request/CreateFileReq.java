package gateway.soap.request;

import java.util.UUID;

public class CreateFileReq extends Operation
{
	public String fileName;
	public byte[] fileContent;
	public UUID location;
}
