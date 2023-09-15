package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Service {
	// auth

	@WebMethod SessionRes login (Credentials credentials);

	@WebMethod SessionRes register(Credentials credentials);

	@WebMethod StatusRes updatePassword (UpdatePasswordReq parameters);

	// file system

	@WebMethod StatusRes createFile (CreateFileReq args);

	@WebMethod StatusRes createDirectory (CreateDirectoryReq args);

	@WebMethod StatusRes deleteFile (DeleteFileReq args);

	@WebMethod ListFileRes listFiles (ListFileReq args);

	@WebMethod DownloadFileRes downloadFile (DownloadFileReq args);

	@WebMethod StatusRes moveFile (MoveFileReq args);

	// sharing

	@WebMethod StatusRes shareWith (ShareWithReq args);

	@WebMethod StatusRes unShareWith (UnShareWithReq args);

	@WebMethod SharedWithWhoRes sharedWithWho (SharedWithWhoReq args);

	@WebMethod ListSharedWithMeRes listSharedWithMe (Authorization auth);
}
