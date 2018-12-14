package com.ucloud.library.netanalysis.api.interceptor;

import android.util.Base64;

import com.google.gson.JsonObject;
import com.ucloud.library.netanalysis.utils.Encryptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Joshua_Yin on 2018/12/14 15:38.
 * Company: Gemii Tech
 * E-mail: joshua.yin@ucloud.cn
 */

public class EncryptInterceptor implements Interceptor {
    private String TAG = getClass().getSimpleName();
    
    private String publicKey;
    private String appId;
    
    public EncryptInterceptor(String publicKey, String appId) {
        this.publicKey = publicKey;
        this.appId = appId;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        if (originalRequest.body() == null)
            return chain.proceed(originalRequest);
        
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        new EncryptRequestBody(originalRequest.body()))
                .build();
        
        return chain.proceed(progressRequest);
    }
    
    public class EncryptRequestBody extends RequestBody {
        protected RequestBody oriReqBody;
        protected JsonObject encryptData;
        
        public EncryptRequestBody(RequestBody oriReqBody) {
            this.oriReqBody = oriReqBody;
            encrypt();
        }
        
        @Override
        public MediaType contentType() {
            return oriReqBody.contentType();
        }
        
        @Override
        public long contentLength() {
            if (oriReqBody == null)
                return 0l;
            
            return encryptData.toString().length();
        }
        
        private String readRequestBody(RequestBody body) {
            if (body == null)
                return "";
            
            Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);
                return buffer.readUtf8();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        
        private void encrypt() {
            encryptData = new JsonObject();
            // 将源数据进行RSA加密后放入RequestBody
            String cryptograph = "";
            try {
                byte[] cryptRes = Encryptor.encryptRSA(readRequestBody(oriReqBody).getBytes(Charset.forName("UTF-8")),
                        Encryptor.getPublicKey(publicKey.getBytes(Charset.forName("UTF-8"))));
                cryptograph = Base64.encodeToString(cryptRes, Base64.DEFAULT);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } finally {
                encryptData.addProperty("data", cryptograph);
                encryptData.addProperty("app_id", appId);
            }
        }
        
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            doWriteTo(sink, Okio.source(new ByteArrayInputStream(encryptData.toString().getBytes(Charset.forName("UTF-8")))));
        }
        
        private void doWriteTo(BufferedSink sink, Source source) throws IOException {
            long read;
            
            try {
                while ((read = source.read(sink.buffer(), 32 << 10)) > 0)
                    sink.flush();
            } finally {
                if (source != null)
                    source.close();
            }
        }
    }
}
