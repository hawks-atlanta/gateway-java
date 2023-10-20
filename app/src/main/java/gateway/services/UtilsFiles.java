package gateway.services;

import gateway.soap.response.File;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class UtilsFiles
{
	public static File[] createFileArray (JSONArray jsonArray)
	{
		File[] files = new File[jsonArray.length ()];

		for (int i = 0; i < jsonArray.length (); i++) {
			JSONObject fileObject = jsonArray.getJSONObject (i);
			File file = new File ();

			file.uuid = UUID.fromString (fileObject.getString ("uuid"));
			file.name = fileObject.getString ("fileName");
			file.extension =
				fileObject.isNull ("fileExtension") ? null : fileObject.getString ("fileExtension");
			file.isFile = fileObject.getString ("fileType").equals ("archive");
			file.size = fileObject.getInt ("fileSize");

			files[i] = file;
		}

		return files;
	}
}
