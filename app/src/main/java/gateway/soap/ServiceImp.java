package gateway.soap;

import gateway.soap.environment.*;
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

@WebService(endpointInterface = "gateway.soap.Service")
public class ServiceImp implements Service {
	// auth

	@WebMethod
	public SessionRes login(Credentials credentials) {
		// TODO: Replace me

		// Example handle JSON

		String string_res = "{\"succeed\":false,\"msg\":\"example message\"}";
		JSONObject json_res = new JSONObject(string_res);

		System.out.println("---");
		System.out.println(json_res.getBoolean("succeed"));
		System.out.println(json_res.getString("msg"));

		// Example return type

		SessionRes res = new SessionRes();
		res.auth = new Authorization();
		res.auth.token = "sample-token-for-" + credentials.username;
		res.success = true;
		res.message = "Successfully logged in";

		return res;
	}

	// user register in auth service, returns an access token
	@WebMethod
	public SessionRes register(Credentials credentials) {
		SessionRes res = new SessionRes();

		String url = EnvironmentVar.AUTH_BASEURL + "register";

		String credentialsString = "{\"username\":\"" + credentials.username +
				"\",\"password\":\"" + credentials.password + "\"}";

		try {
			// Configs and make HTTP POST request to user register.
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.POST(BodyPublishers.ofString(credentialsString))
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JSONObject jsonObject = new JSONObject(response.body());
			res.auth = new Authorization();
			res.auth.token = jsonObject.getString("jwt");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return res;
	}

	@WebMethod
	public StatusRes updatePassword(UpdatePasswordReq parameters) {
		return new StatusRes();
	}

	// file system

	@WebMethod
	public StatusRes createFile(CreateFileReq args) {
		// TODO: Replace me
		System.out.println("---");
		System.out.println(args);
		System.out.println(args.token);
		System.out.println(args.fileName);
		System.out.println("---");
		StatusRes s = new StatusRes();
		s.success = true;
		s.message = "File created successfully";
		return s;
	}

	@WebMethod
	public StatusRes createDirectory(CreateDirectoryReq args) {
		return new StatusRes();
	}

	@WebMethod
	public StatusRes deleteFile(DeleteFileReq args) {
		return new StatusRes();
	}

	@WebMethod
	public ListFileRes listFiles(ListFileReq args) {
		return null;
	}

	@WebMethod
	public DownloadFileRes downloadFile(DownloadFileReq args) {
		return null;
	}

	@WebMethod
	public StatusRes moveFile(MoveFileReq args) {
		return new StatusRes();
	}

	// sharing

	@WebMethod
	public StatusRes shareWith(ShareWithReq args) {
		return new StatusRes();
	}

	@WebMethod
	public StatusRes unShareWith(UnShareWithReq args) {
		return new StatusRes();
	}

	@WebMethod
	public SharedWithWhoRes sharedWithWho(SharedWithWhoReq args) {
		return null;
	}

	@WebMethod
	public ListSharedWithMeRes listSharedWithMe(Authorization auth) {
		return null;
	}
}
