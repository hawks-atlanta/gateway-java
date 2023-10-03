package gateway.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.json.JSONObject;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.services.ServiceAuth.ResUUID;
import gateway.soap.request.ReqShareFile;
import gateway.soap.response.ResStatus;

public class CtrlShareFile {
    public static ResStatus share_file(ReqShareFile args) {
        ResStatus statusRes = new ResStatus();

        try {
            // validations
            ResStatus resValidate = UtilValidator.validate(args);
            if (resValidate.error) {
                return ResStatus.downCast(ResStatus.class, resValidate);
            }

            // obtain uuid from user and otheruser
            UUID userUUID = UUID.fromString(ServiceAuth.tokenGetClaim(args.token, "uuid"));
            // TODO VALIDATE SOMETHING?
            ResUUID otherUserUUID = ServiceAuth.getUserUUID(args.token, args.otherUsername);

            JSONObject requestBody = new JSONObject();
            requestBody.put("otherUserUUID", otherUserUUID.uuid);

            // request to share file with otheruser
            String url = Config.getMetadataBaseUrl() + "/api/v1/files/share/" + userUUID + "/" +
                    args.fileUUID;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(BodyPublishers.ofString(requestBody.toString()))
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .build();

            // Response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            statusRes.code = response.statusCode();

            if (statusRes.code == 204) {
                statusRes.error = false;
                statusRes.msg = "The file have been shared";
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                statusRes.error = true;
                statusRes.msg = jsonObject.getString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusRes.code = 500;
            statusRes.error = true;
            statusRes.msg = "Internal server error. Try again later";
        }

        return statusRes;
    }
}
// TODO WORKER?
