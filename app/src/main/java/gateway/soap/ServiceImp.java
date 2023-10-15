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

	@WebMethod public ResSession auth_refresh (Authorization auth)
	{
		return CtrlAuthRefresh.auth_refresh (auth);
	}

	// account

	@WebMethod public ResSession account_register (Credentials credentials)
	{
		return CtrlAccountRegister.account_register (credentials);
	}

	@WebMethod public ResStatus account_password (ReqAccPassword parameters)
	{
		return CtrlUpdatePassword.account_password (parameters);
	}

	// file system

	@WebMethod public ResFileNew file_upload (ReqFileUpload args)
	{
		return CtrlFileUpload.file_upload (args);
	}

	@WebMethod public ResFileNew file_new_dir (ReqFileNewDir args)
	{
		return CtrlFileNewDir.file_new_dir (args);
	}

	@Override public ResFileGet file_get (ReqFile args) { return CtrlFileGet.file_get (args); }

	@WebMethod public ResFileCheck file_check (ReqFile args)
	{
		return CtrlFileCheck.file_check (args);
	}

	@WebMethod public ResStatus file_delete (ReqFileDelete args) { return null; }

	@WebMethod public ResFileList file_list (ReqFileList args)
	{
		return CtrlFileList.file_list (args);
	}

	@WebMethod public ResFileDownload file_download (ReqFile args)
	{
		return CtrlFileDownload.file_download (args);
	}

	@WebMethod public ResStatus file_move (ReqFileMove args)
	{
		return CtrlFileMove.file_move (args);
	}

	@WebMethod public ResStatus file_rename (ReqFileRename args)
	{
		return CtrlFileRename.file_rename (args);
	}

	// sharing

	@WebMethod public ResStatus share_file (ReqShareFile args)
	{
		return CtrlShareFile.share_file (args);
	}

	@WebMethod public ResStatus unshare_file (ReqShareRemove args) { return null; }

	@WebMethod public ResShareList share_list (Authorization auth) { return null; }

	@WebMethod public ResShareListWithWho share_list_with_who (ReqFile args) { return null; }
}
