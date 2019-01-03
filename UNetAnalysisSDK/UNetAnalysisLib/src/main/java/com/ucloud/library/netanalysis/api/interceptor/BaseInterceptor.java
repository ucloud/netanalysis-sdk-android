package com.ucloud.library.netanalysis.api.interceptor;

import com.ucloud.library.netanalysis.utils.JLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Joshua_Yin on 2018/9/11 15:38.
 * Company: Gemii Tech
 * E-mail: joshua.yin@ucloud.cn
 */

public class BaseInterceptor implements Interceptor {
    private String TAG = getClass().getSimpleName();
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获得请求信息，此处如有需要可以添加headers信息
        Request request = chain.request();
        
        //添加Cookie信息
//        request.newBuilder().addHeader("Cookie", "aaaa");
        
        //打印请求信息
        JLog.T(TAG, "[request]:" + request.toString());
        JLog.T(TAG, "[request-headers]:" + request.headers().toString());
        JLog.T(TAG, "[request-body]:" + readRequestBody(request));
        
        //记录请求耗时
        long startNs = System.nanoTime();
        Response response;
        try {
            //发送请求，获得相应，
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        //打印请求耗时
        JLog.V(TAG, "[耗时]:" + tookMs + "ms");
        //使用response获得headers(),可以更新本地Cookie。
        JLog.T(TAG, "[response-code]:" + response.code());
        JLog.T(TAG, "[response-headers]:" + response.headers().toString());
        
        //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
        ResponseBody responseBody = response.body();
        
        //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        //获得返回的数据
        Buffer buffer = source.buffer();
        //使用前clone()下，避免直接消耗
        JLog.T(TAG, "[response-body]:" + buffer.clone().readString(Charset.forName("UTF-8")));
        
        return response;
    }
    
    private String readRequestBody(Request oriReq) {
        if (oriReq.body() == null)
            return "";
        
        Request request = oriReq.newBuilder().build();
        Buffer buffer = new Buffer();
        try {
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "";
    }
}
