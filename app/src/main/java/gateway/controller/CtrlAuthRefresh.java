package gateway.controller;

import gateway.config.Config;
import gateway.soap.request.Authorization;
import gateway.soap.response.ResSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class CtrlAuthRefresh
{

	public static ResSession auth_refresh (Authorization authorization)
	{
		ResSession res = new ResSession ();
		String url = Config.getAuthBaseUrl () + "/challenge";

		try {

			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.POST (HttpRequest.BodyPublishers.noBody ())
					.uri (URI.create (url))
					.header ("Authorization", "Bearer " + authorization.token)
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			res.code = response.statusCode ();

			if (res.code == 200) {
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
