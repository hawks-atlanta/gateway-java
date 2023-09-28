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
	public static class ResSaveFile extends ResStatus
	{
		public UUID fileUUID;
	}

	public static ResSaveFile
	saveFile (UUID userUUID, UUID directoryUUID, String filetype, String filename, int filesize)
	{

		ServiceMetadata.ResSaveFile s = new ServiceMetadata.ResSaveFile ();
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
					.uri (URI.create (String.format ("%s/files", Config.getMetadataBaseUrl ())))
					.POST (BodyPublishers.ofString (body.toString ()))
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// response
			JSONObject resBody = new JSONObject (res.body ());
			s.code = res.statusCode ();
			s.error = true;

			if (s.code == 201) {
				s.error = false;
				s.fileUUID = UUID.fromString (resBody.getString ("uuid"));
			} else {
				s.msg = resBody.getString ("message");
			}
		} catch (Exception e) {
			System.err.println (e);
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}

	public static ResStatus canRead (UUID userUUID, UUID fileUUID)
	{
		ResStatus s = new ResStatus ();

		try {
			HttpResponse<String> res = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (String.format (
						"%s/files/can_read/%s/%s", Config.getMetadataBaseUrl (),
						userUUID.toString (), fileUUID.toString ())))
					.GET ()
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// response
			s.code = res.statusCode ();
			s.error = true;

			if (s.code == 204) {
				s.error = false;
			} else {
				JSONObject resBody = new JSONObject (res.body ());
				s.msg = resBody.getString ("message");
			}
		} catch (Exception e) {
			System.err.println (e);
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}

	public static class ResFileMetadata extends ResStatus
	{
		public String name;
		public String extension;
		public int volume;
		public long size;
		public boolean isShared;
	}

	public static ResFileMetadata getFileMetadata (UUID fileUUID)
	{
		ResFileMetadata s = new ResFileMetadata ();

		try {
			HttpResponse<String> res = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (String.format (
						"%s/files/metadata/%s", Config.getMetadataBaseUrl (),
						fileUUID.toString ())))
					.GET ()
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// response
			JSONObject resBody = new JSONObject (res.body ());
			s.code = res.statusCode ();
			s.error = true;

			if (s.code == 200) {
				s.error = false;
				s.name = resBody.getString ("name");
				s.extension = resBody.getString ("extension");
				s.volume = resBody.getInt ("volume");
				s.size = resBody.getLong ("size");
				s.isShared =
					resBody.getBoolean ("is_shared"); // TODO make a test for true and false
			} else {
				s.msg = resBody.getString ("message");
			}
		} catch (Exception e) {
			System.err.println (e);
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}
}
