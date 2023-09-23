package gateway.soap.request;

import java.util.UUID;

public class ReqFileUpload extends Authorization
{
	public String fileName;
	public byte[] fileContent;
	public UUID location;
}
