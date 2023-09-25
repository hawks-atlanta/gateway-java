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

			// response
			JSONObject jsonObject = new JSONObject (response.body ());
			s.code = response.statusCode();
			s.msg = jsonObject.getString ("msg");
			s.error = true;

			if (s.code == 200) {
				s.error = false;
			}
		} catch (Exception e) {
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}

	public static UUID getUserUUID (String token, String username) throws Exception
	{
		try {
			String uri = String.format ("%s/user/uuid/%s", Config.getAuthBaseUrl (), username);

			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (uri))
					.GET ()
					.header ("Authorization", "Bearer " + token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 200) {
				return UUID.fromString (jsonObject.getString ("uuid"));
			}
		} catch (IOException | InterruptedException e) {
			throw e;
		}

		throw new Exception ("Internal server error");
	}
}
