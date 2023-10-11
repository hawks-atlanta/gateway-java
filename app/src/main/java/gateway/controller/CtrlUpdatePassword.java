package gateway.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqAccPassword;
import gateway.soap.response.ResFileDownload;
import gateway.soap.response.ResSession;
import gateway.soap.response.ResStatus;

public class CtrlUpdatePassword {
    public static ResStatus account_password (ReqAccPassword accPassword) {

        // Create a new ResSession object to hold the response data
		ResSession resSession = new ResSession ();

		// Check fields of ReqAccPassword
        ResStatus resValidate = UtilValidator.validate (accPassword);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileDownload.class, resValidate);
		}
        
        // auth

		ResStatus resAuth = ServiceAuth.authenticate (accPassword.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileDownload.class, resAuth);
		}

        // Define the URL for updated password
		String url = Config.getAuthBaseUrl () + "/account/password";

        // Create a JSONObject to hold the request body data.
		JSONObject requestBody = new JSONObject ();

        // Add the new password and old password fields
		// request body
		requestBody.put ("oldPassword", accPassword.oldpassword);
		requestBody.put ("newPassword", accPassword.newpassword);

        try {
			HttpResponse<String> response = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder()
					.uri(URI.create (url))
					.method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody.toString ()))
					.header("Authorization", "Bearer " + accPassword.token)
					.header("Content-Type", "application/json")
					.build(),
				HttpResponse.BodyHandlers.ofString()
			);

            // Create a JSONObject to hold the response body received from the HTTP request.
			JSONObject jsonObject = new JSONObject (response.body ());

			// Get the HTTP status code from the response.
			resSession.code = response.statusCode ();

            // Check if the response status code is 200 (Updated Password)
			if (resSession.code == 200) {
				// If the status code is 200, indicating Password updated successfully
				resSession.error = false;
				resSession.msg = jsonObject.getString ("msg");
			} else {
				// If the status code is different from 201, indicating an error response, extract
				// success status and message from the JSON object.
				resSession.error = true;
				resSession.msg = jsonObject.getString ("msg");
			}

        } catch (Exception e){
            // Handle exceptions if occur.
			resSession.code = 500;
			resSession.error = true;
			resSession.msg = "Internal error, try again later";
        }

        // Return the ResSession object containing the response data.
		return resSession;
    }
}
