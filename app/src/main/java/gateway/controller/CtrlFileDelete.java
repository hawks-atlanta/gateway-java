package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFileDelete;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class CtrlFileDelete
{
	public static ResStatus file_delete (ReqFileDelete args)
	{

		// Create a new ResStatus object to hold the response data
		ResStatus resStatus = new ResStatus ();

		// Check fields of ReqFileDelete
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return resValidate;
		}

		// Auth
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return resAuth;
		}

		// obtain uuid from user
		UUID userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (String.format (
						"%s/files/delete/%s/%s", Config.getMetadataBaseUrl (), userUUID.toString (),
						args.fileUUID.toString ())))
					.DELETE ()
					.build (),
				HttpResponse.BodyHandlers.ofString ());
			// Get the HTTP status code from the response
			resStatus.code = response.statusCode ();
			resStatus.error = true;
			if (resStatus.code == 204) {
				// If the response code is 204, the request was successful
				resStatus.error = false;
			} else {
				// If the response code is not 204, there was an error
				JSONObject responseBody = new JSONObject (response.body ());
				resStatus.msg = responseBody.getString ("message");
				return resStatus;
			}
		} catch (Exception e) {
			// In case of an exception, handle the error
			e.printStackTrace ();
			resStatus.code = 500;
			resStatus.error = true;
			resStatus.msg = "Internal server error. Try again later";
			return resStatus;
		}

		return resStatus;
	}
}
