package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.soap.request.Authorization;
import gateway.soap.response.ResShareList;
import gateway.soap.response.SharedFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class CtrlShareList
{
	public static ResShareList share_list (Authorization authorization)
	{
		ResShareList resShareList = new ResShareList ();

		// obtain uuid from user and otheruser
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
			JSONObject jsonObject = new JSONObject (response.body ());
			resShareList.code = response.statusCode ();

			if (resShareList.code == 200) {
				resShareList.error = false;
				resShareList.sharedFiles =
					new SharedFile[jsonObject.getJSONArray ("files").length ()];
				for (int i = 0; i < jsonObject.getJSONArray ("files").length (); i++) {
					JSONObject fileJson = jsonObject.getJSONArray ("files").getJSONObject (i);
					resShareList.sharedFiles[i] = convertJsonToFile (fileJson);
				}
				resShareList.msg = "Ok. The directory was listed.";
			} else {
				resShareList.error = true;
				resShareList.msg = jsonObject.getString ("msg");
			}

		} catch (Exception e) {
			e.printStackTrace ();
			resShareList.code = 500;
			resShareList.error = true;
			resShareList.msg = "Internal error, try again later";
		}

		return resShareList;
	}

	private static SharedFile convertJsonToFile (JSONObject fileJson)
	{
		SharedFile sharedFile = new SharedFile ();
		sharedFile.name = fileJson.getString ("name");
		sharedFile.isFile = fileJson.getBoolean ("isFile");
		sharedFile.uuid = UUID.fromString (fileJson.getString ("uuid"));
		sharedFile.size = fileJson.getInt ("size");

		sharedFile.ownerUsername = fileJson.getString ("ownerUsername");

		return sharedFile;
	}
}
