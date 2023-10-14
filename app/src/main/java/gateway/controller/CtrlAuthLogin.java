package gateway.controller;

import gateway.config.Config;
import gateway.services.UtilValidator;
import gateway.soap.request.*;
import gateway.soap.response.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class CtrlAuthLogin
{
	public static ResSession auth_login (Credentials credentials)
	{
		// Create a new ResSession object to hold the response data
		ResSession res = new ResSession ();

		ResStatus resValidate = UtilValidator.validate (credentials);
		if (resValidate.error) {
			return ResStatus.downCast (ResSession.class, resValidate);
		}

		// Define the URL for the authentication request
		String url = Config.getAuthBaseUrl () + "/login";

		// Create a JSONObject to hold the request body data.
		JSONObject requestBody = new JSONObject ();

		// Add the username and password fields obtained from the credentials object to the JSON
		// request body
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.POST (BodyPublishers.ofString (requestBody.toString ()))
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Create a JSONObject to hold the response body received from the HTTP request.
			JSONObject jsonObject = new JSONObject (response.body ());

			// Get the HTTP status code from the response.
			res.code = response.statusCode ();

			// Check if the response status code is 201 (Login succeed)
			if (res.code == 201) {
				// If the status code is 201, indicating login succeed, initialize an Authorization
				// object in the response and extract the JWT token.
				res.auth = new Authorization ();
				res.auth.token = jsonObject.getString ("jwt");
				res.error = false;
				res.msg = "Login succeed";
			} else {
				// If the status code is different from 201, indicating an error response, extract
				// success status and message from the JSON object.
				res.error = true;
				res.msg = jsonObject.getString ("msg");
			}
		} catch (Exception e) {
			// Handle exceptions such as IOException and InterruptedException, if they occur.
			System.err.println (e);
			res.code = 500;
			res.error = true;
			res.msg = "Internal error, try again later";
		}

		// Return the res object containing the response data.
		return res;
	}
}
