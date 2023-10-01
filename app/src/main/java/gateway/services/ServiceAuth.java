package gateway.services;

import com.auth0.jwt.JWT;
import gateway.config.Config;
import gateway.soap.request.Authorization;
import gateway.soap.response.ResSession;
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
	public static String tokenGetClaimStr (String token, String claim)
	{
		return JWT.decode (token).getClaim (claim).asString ();
	}

	public static Long tokenGetClaimLong (String token, String claim)
	{
		return JWT.decode (token).getClaim (claim).asLong ();
	}

	public static ResSession authenticate (String token)
	{
		ResSession s = new ResSession ();

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
				s.auth = new Authorization ();
				s.auth.token = jsonObject.getString ("jwt");
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
}
