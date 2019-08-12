package com.ucloud.library.netanalysis.api.http;

import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.parser.JsonDeserializer;
import com.ucloud.library.netanalysis.utils.JLog;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by joshua on 2019-07-07 16:54.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCHttpsClient extends UCHttpClient {
    
    public <T> Response<T> execute(Request request, JsonDeserializer<T> deserializer) throws UCHttpException {
        if (request == null)
            throw new UCHttpException("request can not be null");
        HttpsURLConnection connection ;
        try {
            connection = (HttpsURLConnection) request.url().openConnection();
            connection.setConnectTimeout(timeoutConnect);
            connection.setReadTimeout(timeoutRead);
            return connect(connection, request, deserializer);
        } catch (KeyManagementException | IOException | NoSuchAlgorithmException | JSONException e) {
            JLog.D(TAG, "https request occur error: " + e.getMessage());
            throw new UCHttpException(e);
        }
    }
}
