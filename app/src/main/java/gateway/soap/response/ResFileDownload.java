package gateway.soap.response;

import java.util.UUID;

public class ResFileDownload extends ResStatus
{
	public UUID fileUUID;
	public String fileName;
	public byte[] fileContent;
}
