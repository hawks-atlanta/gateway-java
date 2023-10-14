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
		ResFileList resFileList = new ResFileList ();
		UUID userUUID;
		String url;

		// auth
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileList.class, resAuth);
		}

		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		if (args.location == null || args.location.equals (new UUID (0L, 0L))) {
			url = Config.getMetadataBaseUrl () + "/files/list/" + userUUID;
		} else {
			url = Config.getMetadataBaseUrl () + "/files/list/" + userUUID +
				  "?parentUUID=" + args.location;
		}

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ().uri (URI.create (url)).GET ().build (),
				HttpResponse.BodyHandlers.ofString ());

			JSONObject responseBody = new JSONObject (response.body ());
			resFileList.code = response.statusCode ();

			if (resFileList.code == 200) {
				JSONArray filesArray = responseBody.getJSONArray ("files");
				File[] files = new File[filesArray.length ()];

				for (int i = 0; i < filesArray.length (); i++) {
					JSONObject fileObject = filesArray.getJSONObject (i);

					String fileuuid = fileObject.getString ("uuid");
					String fileType = fileObject.getString ("fileType");
					String fileName = fileObject.getString ("name");
					String fileExtension =
						fileObject.isNull ("extension") ? null : fileObject.getString ("extension");

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
				resFileList.error = true;
				resFileList.msg = responseBody.getString ("message");
			}
		} catch (Exception e) {
			e.printStackTrace ();
			resFileList.code = 500;
			resFileList.error = true;
			resFileList.msg = "Internal error, try again later";
		}

		return resFileList;
	}
}
