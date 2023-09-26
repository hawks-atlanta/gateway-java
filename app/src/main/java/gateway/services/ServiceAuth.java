package gateway.services;

import gateway.config.Config;
import gateway.soap.response.ResStatus;
import java.io.IOException;
import java.lang.Exception;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class ServiceAuth
{
	public static ResStatus authenticate (String token)
	{
		ResStatus s = new ResStatus ();

		// post

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (Config.getAuthBaseUrl () + "/challenge"))
					.POST (HttpRequest.BodyPublishers.noBody ())
					.header ("Authorization", "Bearer " + token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// handle it

			JSONObject jsonObject = new JSONObject (response.body ());
			System.out.println (jsonObject);
			s.code = response.statusCode ();
			s.error = true;

			if (s.code == 200) {
				s.error = false;
			} else {
				s.msg = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			e.printStackTrace ();
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}

	public static class ResUUID extends ResStatus
	{
		public UUID uuid;
	}

	public static ResUUID getUserUUID (String token, String username)
	{
		ResUUID s = new ResUUID ();

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (
						String.format ("%s/user/uuid/%s", Config.getAuthBaseUrl (), username)))
					.GET ()
					.header ("Authorization", "Bearer " + token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			JSONObject jsonObject = new JSONObject (response.body ());
			s.code = response.statusCode ();
			s.error = true;

			if (s.code == 200) {
				s.error = false;
				s.uuid = UUID.fromString (jsonObject.getString ("uuid"));
			} else {
				s.msg = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			e.printStackTrace ();
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}
}
