package com.ucloud.library.netanalysis.api.http;

import androidx.collection.ArrayMap;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.UmqaClient;
import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by joshua on 2019-07-07 19:15.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Request<T> {
    private final String TAG = getClass().getSimpleName();
    private String urlStr;
    private URL url;
    private HttpMethod method;
    private Map<String, String> query;
    private Map<String, String> headers;
    private byte[] body;
    private T bodyObj;
    
    private Request() {
    }
    
    public RequestBuilder<T> newBuilder() {
        String port = url.getPort() == -1 ? "" : (":" + url.getPort());
        RequestBuilder<T> builder = new RequestBuilder<>(url.getHost() + port, method);
        builder.path(url.getPath());
        builder.query(query);
        builder.headers(headers);
        builder.body(bodyObj);
        builder.userAgent(headers.get("User-Agent"));
        builder.contentType(headers.get("Content-Type"));
        return builder;
    }
    
    public String urlStr() {
        return urlStr;
    }
    
    public URL url() {
        return url;
    }
    
    public HttpMethod method() {
        return method;
    }
    
    public Map<String, String> query() {
        return query;
    }
    
    public Map<String, String> headers() {
        return headers;
    }
    
    public byte[] body() {
        return body;
    }
    
    public T bodyObj() {
        return bodyObj;
    }
    
    public static class RequestBuilder<T> {
        private final String TAG = getClass().getSimpleName();
        private URL url;
        private HttpProtocol protocol;
        private String host;
        private String path;
        private HttpMethod method;
        private Map<String, String> headers;
        private String userAgent;
        private String contentType;
        private Map<String, String> query;
        private T body;
        
        public RequestBuilder(URL url) {
            this.url = url;
        }
        
        public RequestBuilder(String host, HttpMethod method) {
            host(host);
            this.method = method;
            this.headers = new ArrayMap<>();
            this.query = new ArrayMap<>();
        }
        
        public RequestBuilder<T> url(URL url) {
            this.url = url;
            return this;
        }
        
        public RequestBuilder<T> url(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                this.url = null;
            }
            return this;
        }
        
        public RequestBuilder<T> host(String host) {
            this.host = host;
            if (host.startsWith("https://"))
                protocol = HttpProtocol.HTTPS;
            else
                protocol = HttpProtocol.HTTP;
            if (host.contains("://")) {
                int index = host.indexOf("://");
                this.host = host.substring(index + 3);
            }
            
            return this;
        }
        
        public RequestBuilder<T> path(String path) {
            this.path = path;
            return this;
        }
        
        public RequestBuilder<T> userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public RequestBuilder<T> contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }
        
        public RequestBuilder<T> headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
        
        public RequestBuilder<T> addHeader(String key, String val) {
            if (TextUtils.isEmpty(key))
                return this;
            
            if (headers == null)
                headers = new ArrayMap<>();
            headers.put(key, val);
            return this;
        }
        
        public RequestBuilder<T> query(Map<String, String> query) {
            this.query = query;
            return this;
        }
        
        public RequestBuilder<T> addQuery(String key, String val) {
            if (TextUtils.isEmpty(key))
                return this;
            
            if (query == null)
                query = new ArrayMap<>();
            query.put(key, val);
            return this;
        }
        
        public RequestBuilder<T> body(T body) {
            this.body = body;
            return this;
        }
        
        private String urlEncode(String str) throws UCHttpException {
            return urlEncode(str, null);
        }
        
        private String urlEncode(String str, String separator) throws UCHttpException {
            return urlEncode(str, separator, null);
        }
        
        private String urlEncode(String str, String separator, String keep) throws UCHttpException {
            if (TextUtils.isEmpty(str))
                return "";
            
            if (TextUtils.isEmpty(separator)) {
                try {
                    return URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new UCHttpException(e);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                String[] parts = str.split(separator);
                if (str.indexOf(separator) == 0)
                    sb.append("/");
                try {
                    for (String part : parts) {
                        if (TextUtils.isEmpty(part))
                            continue;
                        
                        if (!TextUtils.isEmpty(keep)) {
                            sb.append(urlEncode(part, keep, null));
                            sb.append(separator);
                        } else {
                            sb.append(URLEncoder.encode(part, "UTF-8"));
                            sb.append(separator);
                        }
                    }
                    
                    if (str.lastIndexOf(separator) < (str.length() - 1))
                        return sb.substring(0, sb.length() - 1);
                    
                    return sb.toString();
                } catch (UnsupportedEncodingException e) {
                    throw new UCHttpException(e);
                }
            }
        }
        
        public byte[] parseBody(T body) {
            if (body == null)
                return null;
            if (body instanceof JsonSerializable)
                return ((JsonSerializable) body).toJson().toString().getBytes(Charset.forName("UTF-8"));
            
            if (body instanceof String)
                return ((String) body).getBytes(Charset.forName("UTF-8"));
            
            return null;
        }
        
        public Request build() throws UCHttpException {
            if (method == null)
                throw new UCHttpException("HttpMethod can not be empty");
            
            Request<T> request = new Request<>();
            request.method = method;
            
            if (url != null) {
                request.url = url;
                String port = url.getPort() == -1 ? "" : (":" + url.getPort());
                String path = url.getPath() == null ? "" : url.getPath();
                String query = url.getQuery() == null ? "" : ("?" + url.getQuery());
                request.urlStr = String.format("%s://%s%s%s%s", url.getProtocol(), url.getHost(), port, path, query);
            } else {
                if (TextUtils.isEmpty(host))
                    throw new UCHttpException("Host can not be empty");
                
                StringBuilder urlBuilder = new StringBuilder(protocol.getProtocol());
                urlBuilder.append(urlEncode(host, "/", ":").trim());
                // TODO: path中有 ? 的暂时无法encoder。 eg：https://fanyi.baidu.com/?aldtype=85#en/zh/a
                urlBuilder.append(urlEncode(path, "/").trim());
                
                if (query != null && !query.isEmpty()) {
                    StringBuilder queryBuilder = new StringBuilder();
                    for (Map.Entry<String, String> entry : query.entrySet()) {
                        String key = entry.getKey();
                        if (TextUtils.isEmpty(key))
                            continue;
                        
                        queryBuilder.append(queryBuilder.length() > 0 ? "&" : "?");
                        queryBuilder.append(urlEncode(key));
                        queryBuilder.append("=");
                        queryBuilder.append(TextUtils.isEmpty(entry.getValue()) ? "" : urlEncode(entry.getValue()));
                    }
                    urlBuilder.append(queryBuilder.toString());
                }
                
                request.query = query;
                request.urlStr = urlBuilder.toString();
                try {
                    request.url = new URL(urlBuilder.toString());
                } catch (MalformedURLException e) {
                    throw new UCHttpException(e);
                }
            }
            
            if (headers == null)
                headers = new ArrayMap<>();
            headers.put("User-Agent", TextUtils.isEmpty(userAgent) ? UmqaClient.SDK_VERSION : userAgent);
            headers.put("Content-Type", TextUtils.isEmpty(contentType) ? "application/json;charset=utf-8" : contentType);
            
            request.headers = headers;
            request.body = parseBody(body);
            request.bodyObj = body;
            
            return request;
        }
    }
}
