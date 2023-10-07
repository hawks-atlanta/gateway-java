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

public class CtrlAccountRegister
{
	// user register in auth service, returns an access token
	public static ResSession account_register (Credentials credentials)
	{
		ResSession res = new ResSession ();

		// validations of all fields
		ResStatus resValidate = UtilValidator.validate (credentials);
		if (resValidate.error) {
			return ResStatus.downCast (ResSession.class, resValidate);
		}

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
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			res.code = response.statusCode ();

			if (res.code == 201) {
				res.auth = new Authorization ();
				res.auth.token = jsonObject.getString ("jwt");
				res.error = false;
				res.msg = "Register succeed";
			} else {
				res.error = true;
				res.msg = jsonObject.getString ("msg");
			}

		} catch (Exception e) {
			e.printStackTrace ();
			res.code = 500;
			res.error = true;
			res.msg = "Internal error, try again later";
		}

		return res;
	}
}
