package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Service {
	// auth

	@WebMethod ResSession auth_login (Credentials credentials);

	@WebMethod ResSession auth_refresh (Authorization auth);

	// account

	@WebMethod ResSession account_register (Credentials credentials);

	@WebMethod ResStatus account_password (ReqAccPassword parameters);

	// file system

	@WebMethod ResFileList file_list (ReqFileList args);

	@WebMethod ResFileNew file_upload (ReqFileUpload args);

	@WebMethod ResFileNew file_new_dir (ReqFileNewDir args);

	@WebMethod ResFileCheck file_check (ReqFile args);

	@WebMethod ResStatus file_delete (ReqFileDelete args);

	@WebMethod ResFileDownload file_download (ReqFile args);

	@WebMethod ResStatus file_move (ReqFileMove args);

	// sharing

	@WebMethod ResStatus share_file (ReqShareFile args);

	@WebMethod ResStatus unshare_file (ReqShareRemove args);

	@WebMethod ResShareList share_list (Authorization auth);

	@WebMethod ResShareListWithWho share_list_with_who (ReqFile args);
}
