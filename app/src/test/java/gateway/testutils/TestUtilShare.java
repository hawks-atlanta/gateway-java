package gateway.testutils;

import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.request.ReqShareRemove;

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

	public static ReqShareRemove
	createShareRemove (ResSaveFile resSaveFile, String otherUsername, String token)
	{
		ReqShareRemove reqShareFile = new ReqShareRemove ();
		reqShareFile.fileUUID = resSaveFile.fileUUID;
		reqShareFile.otherUsername = otherUsername;
		reqShareFile.token = token;
		return reqShareFile;
	}
}
