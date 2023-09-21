package gateway.utils;

import gateway.config.Config;
import gateway.soap.response.StatusRes;
import java.io.IOException;
import java.lang.Exception;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class ManagerAuth
{
	public static StatusRes authenticate (String token)
	{
		StatusRes s = new StatusRes ();

		// post

		try {
			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (Config.getAuthBaseUrl () + "/challenge"))
									  .POST (HttpRequest.BodyPublishers.noBody ())
									  .header ("Authorization", "Bearer " + token)
									  .build ();

			HttpClient client = HttpClient.newHttpClient ();
			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 200) {
				s.success = true;
			} else {
				s.success = false;
				s.message = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			s.success = false;
			s.message = "Internal server error. Try again later";
		}

		return s;
	}

	public static UUID getUserUUID (String token, String username) throws Exception
	{
		try {
			String uri = String.format ("%s/user/uuid/%s", Config.getAuthBaseUrl (), username);
			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (uri))
									  .GET ()
									  .header ("Authorization", "Bearer " + token)
									  .build ();

			HttpClient client = HttpClient.newHttpClient ();
			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());

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
