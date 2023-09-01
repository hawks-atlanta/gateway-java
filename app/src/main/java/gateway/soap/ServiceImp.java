package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public Authorization login (Credentials credentials)
	{
		// TODO: Replace me
		Authorization auth = new Authorization ();
		auth.token = "sample-token-for-" + credentials.username;
		return auth;
	}

	@WebMethod public Authorization register(Credentials credentials)
	{
		return new Authorization ();
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
		return s;
	}

	@WebMethod public StatusRes createDirectory (CreateDirectoryReq args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes deleteFile (DeleteFileReq args) { return new StatusRes (); }

	@WebMethod public File[] listFiles (ListFileReq args) { return new File[0]; }

	@WebMethod public FileContents downloadFile (DownloadFileReq args)
	{
		return new FileContents ();
	}

	@WebMethod public StatusRes moveFile (MoveFileReq args) { return new StatusRes (); }

	// sharing

	@WebMethod public StatusRes shareWith (ShareWithReq args) { return new StatusRes (); }

	@WebMethod public StatusRes unShareWithReq (UnShareWithReq args) { return new StatusRes (); }

	@WebMethod public User[] sharedWithWho (SharedWithWhoReq args) { return new User[0]; }

	@WebMethod public SharedFile[] listSharedWithMe (Authorization auth)
	{
		return new SharedFile[0];
	}
}
