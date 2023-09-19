package gateway.soap;

import gateway.config.Config;
import gateway.soap.request.*;
import gateway.soap.response.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.json.JSONObject;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public SessionRes login (Credentials credentials)
	{
		// Create a new SessionRes object to hold the response data
		SessionRes res = new SessionRes ();

		// Define the URL for the authentication request
		String url = Config.getAuthBaseUrl () + "/login";

		// Create a JSONObject to hold the request body data.
		JSONObject requestBody = new JSONObject ();

		// Add the username and password fields obtained from the credentials object to the JSON request body 
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			// Create an HttpClient instance for making HTTP requests
			HttpClient client = HttpClient.newHttpClient ();

			// Build an HTTP POST request with the specified URL, request body, and headers.
			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (url))
									  .POST (BodyPublishers.ofString (requestBody.toString ()))
									  .uri (URI.create (url))
									  .header ("Content-Type", "application/json")
									  .build ();

			// Send the HTTP request and retrieve the response.
			HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString ());

			// Create a JSONObject to hold the response body received from the HTTP request.
			JSONObject jsonObject = new JSONObject (response.body ());

			// Get the HTTP status code from the response.
			int statusCode = response.statusCode ();

			// Check if the response status code is 201 (Login succeed)
			if (statusCode == 201) {
				// If the status code is 201, indicating login succeed, initialize an Authorization object in the response and extract the JWT token.
				res.auth = new Authorization ();
				res.success = true;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				// If the status code is different from 201, indicating an error response, extract success status and message from the JSON object.
				res.success = jsonObject.getBoolean ("succeed");
				res.message = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			// Handle exceptions such as IOException and InterruptedException, if they occur.
			e.printStackTrace ();
		}

		// Return the res object containing the response data.
		return res;
	}

	// user register in auth service, returns an access token
	@WebMethod public SessionRes register(Credentials credentials)
	{
		SessionRes res = new SessionRes ();

		String url = Config.getAuthBaseUrl () + "/register";

		// Request
		JSONObject requestBody = new JSONObject ();
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			// Configs and make HTTP POST request to user register.
			HttpClient client = HttpClient.newHttpClient ();

			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (url))
									  .POST (BodyPublishers.ofString (requestBody.toString ()))
									  .uri (URI.create (url))
									  .header ("Content-Type", "application/json")
									  .build ();

			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 201) {
				res.auth = new Authorization ();
				res.success = true;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				res.success = jsonObject.getBoolean ("succeed");
				res.message = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace ();
		}

		return res;
	}

	@WebMethod public StatusRes updatePassword (UpdatePasswordReq parameters)
	{
		return new StatusRes ();
	}

	// file system

	@WebMethod public StatusRes createFile (CreateFileReq args)
	{
		// TODO: Replace me
		System.out.println ("---");
		System.out.println (args);
		System.out.println (args.token);
		System.out.println (args.fileName);
		System.out.println ("---");
		StatusRes s = new StatusRes ();
		s.success = true;
		s.message = "File created successfully";
		return s;
	}

	@WebMethod public StatusRes createDirectory (CreateDirectoryReq args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes deleteFile (DeleteFileReq args) { return new StatusRes (); }

	@WebMethod public ListFileRes listFiles (ListFileReq args) { return null; }

	@WebMethod public DownloadFileRes downloadFile (DownloadFileReq args) { return null; }

	@WebMethod public StatusRes moveFile (MoveFileReq args) { return new StatusRes (); }

	// sharing

	@WebMethod public StatusRes shareWith (ShareWithReq args) { return new StatusRes (); }

	@WebMethod public StatusRes unShareWith (UnShareWithReq args) { return new StatusRes (); }

	@WebMethod public SharedWithWhoRes sharedWithWho (SharedWithWhoReq args) { return null; }

	@WebMethod public ListSharedWithMeRes listSharedWithMe (Authorization auth) { return null; }
}
