package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Service {
	// auth

	@WebMethod ResSession login (Credentials credentials);

	@WebMethod ResSession register(Credentials credentials);

	@WebMethod ResStatus updatePassword (ReqUpdatePassword parameters);

	// file system

	@WebMethod ResStatus createFile (CreateFileReq args);

	@WebMethod ResStatus createDirectory (ReqCreateDirectory args);

	@WebMethod ResStatus deleteFile (ReqDeleteFile args);

	@WebMethod ResListFile listFiles (ReqListFile args);

	@WebMethod ResDownloadFile downloadFile (ReqDownloadFile args);

	@WebMethod ResStatus moveFile (ReqMoveFile args);

	// sharing

	@WebMethod ResStatus shareWith (ReqShareWith args);

	@WebMethod ResStatus unShareWith (ReqUnShareWith args);

	@WebMethod ResSharedWithWho sharedWithWho (ReqSharedWithWho args);

	@WebMethod ResListSharedWithMe listSharedWithMe (Authorization auth);
}
