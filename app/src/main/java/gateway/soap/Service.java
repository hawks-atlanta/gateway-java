package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Service {
	// auth

	@WebMethod Authorization login (Credentials credentials);

	@WebMethod Authorization register(Credentials credentials);

	@WebMethod StatusRes updatePassword (UpdatePasswordReq parameters);

	// file system

	@WebMethod StatusRes createFile (CreateFileReq args);

	@WebMethod StatusRes createDirectory (CreateDirectoryReq args);

	@WebMethod StatusRes deleteFile (DeleteFileReq args);

	@WebMethod File[] listFiles (ListFileReq args);

	@WebMethod FileContents downloadFile (DownloadFileReq args);

	@WebMethod StatusRes moveFile (MoveFileReq args);

	// sharing

	@WebMethod StatusRes shareWith (ShareWithReq args);

	@WebMethod StatusRes unShareWith (UnShareWithReq args);

	@WebMethod User[] sharedWithWho (SharedWithWhoReq args);

	@WebMethod SharedFile[] listSharedWithMe (Authorization auth);
}
