package gateway.testutils;

import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.ReqShareFile;

public class TestUtilShare
{
	public static ReqShareFile
	createShareFile (ResSaveFile resSaveFile, String otherUsername, String token)
	{
		ReqShareFile reqShareFile = new ReqShareFile ();
		reqShareFile.fileUUID = resSaveFile.fileUUID;
		reqShareFile.otherUsername = otherUsername;
		reqShareFile.token = token;
		return reqShareFile;
	}
}
