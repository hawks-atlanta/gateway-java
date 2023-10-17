package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.ServiceAuth.ResUsername;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResShareListWithWho;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class CtrlShareListWithWho
{
	public static ResShareListWithWho share_list_with_who (ReqFile args)
	{
		ResShareListWithWho res = new ResShareListWithWho ();

		// Validate Field
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResShareListWithWho.class, resValidate);
		}

		// User authentication
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResShareListWithWho.class, resAuth);
		}

		String url = Config.getMetadataBaseUrl () + "/files/shared_with_who/" + args.fileUUID;

		try {
			// Make an HTTP GET request
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ().uri (URI.create (url)).GET ().build (),
				HttpResponse.BodyHandlers.ofString ());

			// Parse the JSON response
			JSONObject responseBody = new JSONObject (response.body ());
			res.code = response.statusCode ();

			if (res.code == 200) {
				// If the response code is 200, process the received files
				JSONArray sharedWithArray = responseBody.getJSONArray ("shared_with");
				List<String> usernames = new ArrayList<> ();

				for (int i = 0; i < sharedWithArray.length (); i++) {
					// Process each shared_with in the response JSON
					String sharedWithObject = sharedWithArray.getString (i);

					// Get username from UUID
					ResUsername username =
						ServiceAuth.getUsername (args.token, UUID.fromString (sharedWithObject));

					// Check if an error-free user name was obtained
					if (username != null) {
						usernames.add (username.username);
					}
				}

				res.usernames = usernames.toArray (new String[0]);
				res.error = false;
				res.msg = "List of shared with users successfully";
			} else {
				// If the response code is not 200, set error and error message based on the
				// response
				res.error = true;
				res.msg = responseBody.getString ("message");
			}
		} catch (Exception e) {
			// In case of an exception
			e.printStackTrace ();
			res.code = 500;
			res.error = true;
			res.msg = "Internal error, try again later";
		}

		return res;
	}
}
