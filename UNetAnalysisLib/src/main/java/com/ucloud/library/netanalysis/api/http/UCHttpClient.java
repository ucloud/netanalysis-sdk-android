package com.ucloud.library.netanalysis.api.http;

import android.os.SystemClock;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.parser.JsonDeserializer;
import com.ucloud.library.netanalysis.utils.BaseUtil;
import com.ucloud.library.netanalysis.utils.JLog;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by joshua on 2019-07-07 16:54.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCHttpClient {
    protected final String TAG = getClass().getSimpleName();
    
    public static final int DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 20 * 1000;

//    protected final String threadPoolName = "UCHttpClient Dispatcher";
//    protected final boolean daemon = false;
//    protected ExecutorService threadPool;
    
    protected int timeoutConnect = DEFAULT_CONNECT_TIMEOUT;
    protected int timeoutRead = DEFAULT_READ_TIMEOUT;
    
    public UCHttpClient() {
//        threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
//                new SynchronousQueue<Runnable>(), new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable runnable) {
//                Thread result = new Thread(runnable, threadPoolName);
//                result.setDaemon(daemon);
//                return result;
//            }
//        });
    }
    
    public void setTimeoutConnect(int timeoutConnect) {
        this.timeoutConnect = Math.max(60 * 1000, Math.min(1000, timeoutConnect));
    }
    
    public void setTimeoutRead(int timeoutRead) {
        this.timeoutRead = Math.max(60 * 1000, Math.min(1000, timeoutRead));
    }
    
    public <T> Response<T> execute(Request request, JsonDeserializer<T> deserializer) throws UCHttpException {
        if (request == null)
            throw new UCHttpException("request can not be null");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) request.url().openConnection();
            connection.setConnectTimeout(timeoutConnect);
            connection.setReadTimeout(timeoutRead);
            return connect(connection, request, deserializer);
        } catch (KeyManagementException | IOException | NoSuchAlgorithmException | JSONException e) {
            JLog.D(TAG, "http request occur error: " + e.getMessage());
            throw new UCHttpException(e);
        }
    }
    
    protected <T> Response<T> connect(HttpURLConnection connection, Request request, JsonDeserializer<T> deserializer)
            throws KeyManagementException, NoSuchAlgorithmException, IOException, UCHttpException, JSONException {
        connection.setRequestMethod(request.method().name());
        if (connection instanceof HttpsURLConnection) {
            SSLContext sslContext = getSSLContextWithoutCer();
            if (sslContext != null) {
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return TextUtils.equals(hostname, session.getPeerHost());
                    }
                });
            }
        }
        
        URL url = connection.getURL();
        String port = url.getPort() == -1 ? "" : (":" + url.getPort());
        String path = url.getPath() == null ? "" : url.getPath();
        String query = url.getQuery() == null ? "" : ("?" + url.getQuery());
        JLog.D(TAG, String.format("[URL]: %s://%s%s%s%s", url.getProtocol(), url.getHost(), port, path, query));
        
        Map<String, String> headers = request.headers();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        
        Map<String, List<String>> map = connection.getRequestProperties();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            JLog.D(TAG, "[request-header]: " + entry.getKey() + " = " + entry.getValue());
        }
        
        if (request.method().equals(HttpMethod.POST)
                || request.method().equals(HttpMethod.PUT)) {
            if (request.body() != null) {
                if (request.body() != null && request.body().length < 2 << 10)
                    JLog.D(TAG, "[request-body]: " + new String(request.body(), Charset.forName("UTF-8")));
                writeBody(connection, request.body());
            }
        }
        long now = SystemClock.elapsedRealtime();
        connection.connect();
        
        int code = connection.getResponseCode();
        JLog.D(TAG, "[耗时]: " + (SystemClock.elapsedRealtime() - now) + " ms");
        JLog.D(TAG, "[response-code]: " + code);
        
        Response<T> response = new Response<>(code);
        
        // 获取response-header map
        response.setHeaders(connection.getHeaderFields());
        for (Map.Entry<String, List<String>> entry : response.headers().entrySet()) {
            JLog.D(TAG, "[response-header]: " + entry.getKey() + " = " + entry.getValue());
        }
        
        // 读取ContentLength，若没有该字段，赋值 -1
        List<String> cl = response.headers().get("Content-Length");
        long contentLength = cl == null || cl.isEmpty() ? -1 : Long.parseLong(cl.get(0));
        
        // 当code为带数据体的类型时，读取response-body
        if (code >= 200 && code != 204 && code != 304) {
            InputStream inputStream = connection.getInputStream();
            byte[] content = read(inputStream, contentLength);
            
            String bodyContent = content == null ? null : new String(content, Charset.forName("UTF-8"));
            if (content != null && content.length < 2 << 10)
                JLog.D(TAG, "[response-body]: " + bodyContent);
            
            response.setBody(bodyContent == null ? null : deserializer.fromJson(bodyContent));
        }
        
        if (code != 200) {
            // 当code != 200时，读取错误信息
            InputStream errorStream = connection.getErrorStream();
            byte[] error = read(errorStream, contentLength);
            response.setErrorMsg(error == null ? null : new String(error, Charset.forName("UTF-8")));
            if (response.error() != null)
                JLog.D(TAG, "[response-error]: " + response.error());
        }
        
        if (code == 201 || (code >= 300 && code < 400)) {
            // 当code = 3xx时，重定向操作
            String location = connection.getHeaderField("Location");
            JLog.D(TAG, "[Redirect]:" + location);
            connection.disconnect();
            if (TextUtils.isEmpty(location))
                throw new UCHttpException(String.format("response-code = %d redirection, but there is no Location param in response-header"));
            
            Request.RequestBuilder builder = null;
            try {
                URL newUrl = new URL(location);
                builder = request.newBuilder().url(newUrl);
            } catch (MalformedURLException e) {
                builder = request.newBuilder().path(location);
            }
            return execute(builder.build(), deserializer);
        }
        
        connection.disconnect();
        
        return response;
    }
    
    protected void writeBody(HttpURLConnection connection, byte[] body) throws IOException {
        if (body == null || body.length == 0)
            return;
        OutputStream os = connection.getOutputStream();
        os.write(body);
        os.flush();
        BaseUtil.closeAllCloseable(os);
    }
    
    protected final int DEFAULT_BUFFER_SIZE = 4 << 10;
    
    protected byte[] read(InputStream is, long contentLength) throws IOException {
        if (is == null)
            return null;
        
        int bufferSize = Math.max(1, (int) Math.min(contentLength, DEFAULT_BUFFER_SIZE));
        byte[] buffer = null;
        byte[] cache = new byte[bufferSize];
        int len = 0;
        while ((len = is.read(cache)) > 0) {
            if (buffer == null) {
                buffer = Arrays.copyOf(cache, len);
            } else {
                int oldLen = buffer.length;
                byte[] tmp = new byte[oldLen + len];
                System.arraycopy(buffer, 0, tmp, 0, oldLen);
                System.arraycopy(cache, 0, tmp, oldLen, len);
                buffer = tmp;
            }
        }
        BaseUtil.closeAllCloseable(is);
        
        return buffer;
    }
    
    protected static SSLContext getSSLContextWithoutCer() throws NoSuchAlgorithmException, KeyManagementException {
        // 实例化SSLContext
        // 这里参数可以用TSL 也可以用SSL
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{trustManagers}, new SecureRandom());
        return sslContext;
    }
    
    protected static TrustManager trustManagers = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
}
