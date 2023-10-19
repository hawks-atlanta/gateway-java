package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilsFiles;
import gateway.soap.request.Authorization;
import gateway.soap.response.File;
import gateway.soap.response.ResShareList;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class CtrlShareList
{
	public static ResShareList share_list (Authorization authorization)
	{
		ResShareList resShareList = new ResShareList ();

		// obtain uuid from user
		UUID userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (authorization.token, "uuid"));

		String url = Config.getMetadataBaseUrl () + "/files/shared_with_me/" + userUUID;

		try {

			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.GET ()
					.header ("Authorization", "Bearer " + authorization.token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject responseBody = new JSONObject (response.body ());
			resShareList.code = response.statusCode ();

			if (resShareList.code == 200) {
				// If the response code is 200, process the received files
				JSONArray shareFilesArray = responseBody.getJSONArray ("files");
				File[] shareFiles = UtilsFiles.createFileArray (shareFilesArray);

				resShareList.error = false;
				resShareList.sharedFiles = shareFiles;
				resShareList.msg = "Ok. The directory was listed.";
			} else {
				resShareList.error = true;
				resShareList.msg = responseBody.getString ("message");
			}

		} catch (Exception e) {
			e.printStackTrace ();
			resShareList.code = 500;
			resShareList.error = true;
			resShareList.msg = "Internal error, try again later";
		}

		return resShareList;
	}
}
