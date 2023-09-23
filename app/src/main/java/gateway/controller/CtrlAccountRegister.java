package gateway.controller;
import gateway.config.Config;
import gateway.soap.request.*;
import gateway.soap.response.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class CtrlAccountRegister
{
	// user register in auth service, returns an access token
	public static ResSession account_register (Credentials credentials)
	{
		ResSession res = new ResSession ();

		String url = Config.getAuthBaseUrl () + "/register";

		// Request
		JSONObject requestBody = new JSONObject ();
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			// Configs and make HTTP POST request to user register.

			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.POST (BodyPublishers.ofString (requestBody.toString ()))
					.uri (URI.create (url))
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 201) {
				res.auth = new Authorization ();
				res.error = false;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				res.error = true;
				res.msg = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace ();
		}

		return res;
	}
}
