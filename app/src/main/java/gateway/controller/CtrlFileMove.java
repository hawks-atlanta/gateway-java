package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFileMove;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class CtrlFileMove
{
	public static ResStatus file_move (ReqFileMove args)
	{
		ResStatus statusRes = new ResStatus ();

		// validation all fields
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResStatus.class, resValidate);
		}

		// validation auth
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResStatus.class, resAuth);
		}

		// obtain uuid from user
		UUID userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		// request to move file
		JSONObject requestBody = new JSONObject ();
		if (args.targetDirectoryUUID != null) {
			requestBody.put ("parentUUID", args.targetDirectoryUUID);
		} else {
			// move it to root
			requestBody.put ("parentUUID", JSONObject.NULL);
		}

		String url = Config.getMetadataBaseUrl () + "/files/move/" + userUUID + "/" + args.fileUUID;

		try {

			HttpClient client = HttpClient.newHttpClient ();
			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (url))
									  .PUT (BodyPublishers.ofString (requestBody.toString ()))
									  .header ("Content-Type", "application/json")
									  .build ();

			// Response
			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());
			statusRes.code = response.statusCode ();
			System.out.println (response.body ().toString ());
			if (statusRes.code == 204) {
				statusRes.error = false;
				statusRes.msg = "The file have been moved";
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
