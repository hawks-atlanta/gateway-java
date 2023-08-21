package gateway;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface SampleService {
	@WebMethod String getFiles (int userId);

	@WebMethod boolean renameFile (int fileId, String newName);

	@WebMethod boolean deleteFile (int fileId);
}
