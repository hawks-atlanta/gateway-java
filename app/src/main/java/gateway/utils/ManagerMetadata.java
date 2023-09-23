package gateway.utils;

import gateway.config.Config;
import gateway.soap.response.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class ManagerMetadata
{

	// TODO: use inner class
	// class saveFileRes {
	// UUID uuid;
	// String message;
	//}

	public static UUID saveFile (
		ResStatus statusRes, String token, UUID userUUID, UUID directoryUUID, String filetype,
		String filename, int filesize)
	{
		UUID fileUUID = null;

		JSONObject body = new JSONObject ();

		body.put ("userUUID", userUUID);
		body.put ("parentUUID", directoryUUID == null ? JSONObject.NULL : directoryUUID);
		body.put ("fileType", "archive");
		body.put ("fileName", filename);
		body.put ("fileExtension", filetype == null ? JSONObject.NULL : filetype);
		body.put ("fileSize", filesize);

		// post

		try {
			HttpRequest request =
				HttpRequest.newBuilder ()
					.uri (URI.create (Config.getMetadataBaseUrl () + "/api/v1/files"))
					.POST (BodyPublishers.ofString (body.toString ()))
					.header ("Authorization", "Bearer " + token)
					.build ();
			HttpClient client = HttpClient.newHttpClient ();
			HttpResponse<String> res = client.send (request, HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject resBody = new JSONObject (res.body ());

			if (res.statusCode () == 201) {
				fileUUID = UUID.fromString (resBody.getString ("uuid"));
			} else {
				statusRes.message = resBody.getString ("message");
			}
		} catch (Exception e) {
			statusRes.message = "Internal server error. Try again later";
		}

		return fileUUID;
	}
}
