package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.soap.request.ReqFileList;
import gateway.soap.response.File;
import gateway.soap.response.ResFileList;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class CtrlFileList
{
	public static ResFileList file_list (ReqFileList args)
	{
		// Create a new instance of ResFileList to store the results
		ResFileList resFileList = new ResFileList ();
		UUID userUUID;
		String url;

		// User authentication
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileList.class, resAuth);
		}

		// Get the user's UUID from the authentication token
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		// Build the URL for the request.
		if (args.location == null || args.location.equals (new UUID (0L, 0L))) {
			// If no location is specified, build the URL without a parentUUID parameter
			url = Config.getMetadataBaseUrl () + "/files/list/" + userUUID;
		} else {
			// If a location is specified, include the parentUUID parameter
			url = Config.getMetadataBaseUrl () + "/files/list/" + userUUID +
				  "?parentUUID=" + args.location;
		}

		try {
			// Make an HTTP GET request
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ().uri (URI.create (url)).GET ().build (),
				HttpResponse.BodyHandlers.ofString ());

			JSONObject responseBody = new JSONObject (response.body ());
			resFileList.code = response.statusCode ();

			if (resFileList.code == 200) {
				// If the response code is 200, process the received files
				JSONArray filesArray = responseBody.getJSONArray ("files");
				File[] files = new File[filesArray.length ()];

				for (int i = 0; i < filesArray.length (); i++) {
					// Process each file in the response JSON
					JSONObject fileObject = filesArray.getJSONObject (i);

					// Extract file information
					String fileuuid = fileObject.getString ("uuid");
					String fileType = fileObject.getString ("fileType");
					String fileName = fileObject.getString ("name");
					String fileExtension =
						fileObject.isNull ("extension") ? null : fileObject.getString ("extension");

					// Create a File object and assign it the file information
					File file = new File ();

					file.uuid = UUID.fromString (fileuuid);
					file.name = (fileExtension == null) ? fileName : fileName + "." + fileExtension;
					file.isFile = fileType.equals ("archive");
					file.size = 0;

					files[i] = file;
				}

				resFileList.files = files;
				resFileList.error = false;
				resFileList.msg = "Files listed successfully";
			} else {
				// If the response code is not 200, set error and error message based on the
				// response
				resFileList.error = true;
				resFileList.msg = responseBody.getString ("message");
			}
		} catch (Exception e) {
			// In case of an exception
			e.printStackTrace ();
			resFileList.code = 500;
			resFileList.error = true;
			resFileList.msg = "Internal error, try again later";
		}

		return resFileList;
	}
}
