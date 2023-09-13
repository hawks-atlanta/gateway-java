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

@WebService(endpointInterface = "gateway.soap.Service")
public class ServiceImp implements Service {
	// auth

	@WebMethod
	public Authorization login(Credentials credentials) {
		// TODO: Replace me
		Authorization auth = new Authorization();
		auth.token = "sample-token-for-" + credentials.username;
		return auth;
	}

	// user register in auth service, returns an access token
	@WebMethod
	public Authorization register(Credentials credentials) {

		Authorization auth = new Authorization();

		String url = EnvironmentVar.AUTH_BASEURL + "register";

		String credentialsString = "{\"username\":\"" + credentials.username + "\",\"password\":\""
				+ credentials.password + "\"}";

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

			auth.token = response.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return auth;
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
	public File[] listFiles(ListFileReq args) {
		return new File[0];
	}

	@WebMethod
	public FileContents downloadFile(DownloadFileReq args) {
		return new FileContents();
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
	public User[] sharedWithWho(SharedWithWhoReq args) {
		return new User[0];
	}

	@WebMethod
	public SharedFile[] listSharedWithMe(Authorization auth) {
		return new SharedFile[0];
	}
}
