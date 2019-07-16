package com.ucloud.library.netanalysis.api.http;

import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.parser.JsonDeserializer;
import com.ucloud.library.netanalysis.utils.BaseUtil;
import com.ucloud.library.netanalysis.utils.JLog;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by joshua on 2019-07-07 22:27.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Response<T> {
    private final String TAG = getClass().getSimpleName();
    
    private int responseCode;
    private Map<String, List<String>> headers;
    private String errorMsg;
    private T body;
    
    protected Response(int responseCode) {
        this.responseCode = responseCode;
    }
    
    public int responseCode() {
        return responseCode;
    }
    
    public T body() {
        return body;
    }
    
    public Map<String, List<String>> headers() {
        return headers;
    }
    
    public String error() {
        return errorMsg;
    }
    
    void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }
    
    void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    void setBody(T body) {
        this.body = body;
    }
}
