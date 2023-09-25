package gateway.services;

import gateway.config.Config;
import gateway.soap.response.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class ServiceMetadata
{
	public static class ResSaveFile extends ResStatus {
		public UUID fileUUID;
	}

	public static ResSaveFile saveFile (
		UUID userUUID, UUID directoryUUID, String filetype,
		String filename, int filesize)
	{
		
		ServiceMetadata.ResSaveFile s = new ServiceMetadata.ResSaveFile();
		JSONObject body = new JSONObject ();

		body.put ("userUUID", userUUID);
		body.put ("parentUUID", directoryUUID == null ? JSONObject.NULL : directoryUUID);
		body.put ("fileType", "archive");
		body.put ("fileName", filename);
		body.put ("fileExtension", filetype == null ? JSONObject.NULL : filetype);
		body.put ("fileSize", filesize);

		// post

		try {
			HttpResponse<String> res = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (Config.getMetadataBaseUrl () + "/api/v1/files"))
					.POST (BodyPublishers.ofString (body.toString ()))
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// response
			JSONObject resBody = new JSONObject (res.body ());
			s.code = res.statusCode();
			s.error = true;
			s.msg = resBody.getString ("message");

			if (s.code == 201) {
				s.error = false;
				s.fileUUID = UUID.fromString (resBody.getString ("uuid"));
			}
		} catch (Exception e) {
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}
}
