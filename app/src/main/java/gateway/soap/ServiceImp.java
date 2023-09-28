package gateway.soap;

import gateway.controller.*;
import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public ResSession auth_login (Credentials credentials)
	{
		return CtrlAuthLogin.auth_login (credentials);
	}

	@WebMethod public ResSession auth_refresh (Authorization auth) { return null; }

	// account

	@WebMethod public ResSession account_register (Credentials credentials)
	{
		return CtrlAccountRegister.account_register (credentials);
	}

	@WebMethod public ResStatus account_password (ReqAccPassword parameters) { return null; }

	// file system

	@WebMethod public ResFileNew file_upload (ReqFileUpload args)
	{
		return CtrlFileUpload.file_upload (args);
	}

	@WebMethod public ResFileNew file_new_dir (ReqFileNewDir args) { return null; }

	@WebMethod public ResFileCheck file_check (ReqFile args) { return null; }

	@WebMethod public ResStatus file_delete (ReqFileDelete args) { return null; }

	@WebMethod public ResFileList file_list (ReqFileList args) { return null; }

	@WebMethod public ResFileDownload file_download (ReqFile args)
	{
		return CtrlFileDownload.file_download (args);
	}

	@WebMethod public ResStatus file_move (ReqFileMove args) { return null; }

	// sharing

	@WebMethod public ResStatus share_file (ReqShareFile args) { return null; }

	@WebMethod public ResStatus unshare_file (ReqShareRemove args) { return null; }

	@WebMethod public ResShareList share_list (Authorization auth) { return null; }

	@WebMethod public ResShareListWithWho share_list_with_who (ReqFile args) { return null; }
}
