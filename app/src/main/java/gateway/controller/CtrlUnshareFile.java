package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.ServiceAuth.ResUUID;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqShareRemove;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class CtrlUnshareFile
{

	public static ResStatus unshare_file (ReqShareRemove args)
	{
		ResStatus statusRes = new ResStatus ();

		// validations of all fields
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResStatus.class, resValidate);
		}

		// validation of auth
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResStatus.class, resAuth);
		}

		// obtain uuid from user and otheruser
		UUID userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));
		ResUUID otherUserUUID = ServiceAuth.getUserUUID (args.token, args.otherUsername);
		if (otherUserUUID.error) {
			return ResStatus.downCast (ResStatus.class, otherUserUUID);
		}

		// request to share file with otheruser
		JSONObject requestBody = new JSONObject ();
		requestBody.put ("otherUserUUID", otherUserUUID.uuid);

		String url =
			Config.getMetadataBaseUrl () + "/files/unshare/" + userUUID + "/" + args.fileUUID;

		try {

			HttpClient client = HttpClient.newHttpClient ();
			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (url))
									  .POST (BodyPublishers.ofString (requestBody.toString ()))
									  .header ("Content-Type", "application/json")
									  .build ();

			// Response
			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());
			statusRes.code = response.statusCode ();

			if (statusRes.code == 204) {
				statusRes.error = false;
				statusRes.msg = "The file have been unshared";
			} else {
				JSONObject responseBody = new JSONObject (response.body ());
				statusRes.error = true;
				statusRes.msg = responseBody.getString ("message");
			}
		} catch (Exception e) {
			e.printStackTrace ();
			statusRes.code = 500;
			statusRes.error = true;
			statusRes.msg = "Internal error, try again later";
		}

		return statusRes;
	}
}
