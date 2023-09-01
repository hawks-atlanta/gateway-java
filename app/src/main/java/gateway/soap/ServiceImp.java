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

	@WebMethod public StatusRes createFile (Operation<CreateFileReq> args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes createDirectory (Operation<CreateDirectoryReq> args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes deleteFile (Operation<DeleteFileReq> args)
	{
		return new StatusRes ();
	}

	@WebMethod public File[] listFiles (Operation<ListFileReq> args) { return new File[0]; }

	@WebMethod public FileContents downloadFile (Operation<DownloadFileReq> args)
	{
		return new FileContents ();
	}

	@WebMethod public StatusRes moveFile (Operation<MoveFileReq> args) { return new StatusRes (); }

	// sharing

	@WebMethod public StatusRes shareWith (Operation<ShareWithReq> args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes unShareWithReq (Operation<UnShareWithReqReq> args)
	{
		return new StatusRes ();
	}

	@WebMethod public User[] sharedWithWho (Operation<SharedWithWhoReq> args)
	{
		return new User[0];
	}

	@WebMethod public SharedFile[] listSharedWithMe (Authorization auth)
	{
		return new SharedFile[0];
	}
}
