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

	@WebMethod StatusRes createFile (Operation<CreateFileReq> args);

	@WebMethod StatusRes createDirectory (Operation<CreateDirectoryReq> args);

	@WebMethod StatusRes deleteFile (Operation<DeleteFileReq> args);

	@WebMethod File[] listFiles (Operation<ListFileReq> args);

	@WebMethod FileContents downloadFile (Operation<DownloadFileReq> args);

	@WebMethod StatusRes moveFile (Operation<MoveFileReq> args);

	// sharing

	@WebMethod StatusRes shareWith (Operation<ShareWithReq> args);

	@WebMethod StatusRes unShareWithReq (Operation<UnShareWithReqReq> args);

	@WebMethod User[] sharedWithWho (Operation<SharedWithWhoReq> args);

	@WebMethod SharedFile[] listSharedWithMe (Authorization auth);
}
