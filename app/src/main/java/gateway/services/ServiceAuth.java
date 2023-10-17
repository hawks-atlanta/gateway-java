package gateway.services;

import com.auth0.jwt.JWT;
import gateway.config.Config;
import gateway.soap.response.ResStatus;
import java.lang.Exception;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class ServiceAuth
{
	public static String tokenGetClaim (String token, String claim)
	{
		return JWT.decode (token).getClaim (claim).asString ();
	}

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
			s.code = response.statusCode ();
			s.error = true;

			if (s.code == 200) {
				s.error = false;
			} else {
				s.msg = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			System.err.println (e);
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
			System.err.println (e);
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}

	public static class ResUsername extends ResStatus
	{
		public String username;
	}

	public static ResUsername getUsername (String token, UUID uuid)
	{
		// Create an instance of the ResUsername
		ResUsername resUsername = new ResUsername ();

		try {
			// Make an HTTP GET request
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (Config.getAuthBaseUrl () + "/user/username/" + uuid))
					.GET ()
					.header ("Authorization", "Bearer " + token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Parse the JSON response
			JSONObject jsonObject = new JSONObject (response.body ());
			resUsername.code = response.statusCode ();

			if (resUsername.code == 200) {
				// If the response code is 200, the request was successful
				resUsername.error = false;
				resUsername.username = jsonObject.getString ("username");
			} else {
				// If the response code is not 200, there was an error
				resUsername.error = true;
				resUsername.msg = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			// In case of an exception, handle the error
			e.printStackTrace ();
			resUsername.code = 500;
			resUsername.error = true;
			resUsername.msg = "Internal server error. Try again later";
		}

		return resUsername;
	}
}
