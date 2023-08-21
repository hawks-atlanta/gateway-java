package gateway;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService (endpointInterface = "gateway.SampleService")
public class SampleServiceImp implements SampleService
{

	@WebMethod public String getFiles (int userId) { return "helloworld.txt"; }

	@WebMethod public boolean renameFile (int fileId, String newName)
	{
		return true;
	}

	@WebMethod public boolean deleteFile (int fileId) { return false; }
}
