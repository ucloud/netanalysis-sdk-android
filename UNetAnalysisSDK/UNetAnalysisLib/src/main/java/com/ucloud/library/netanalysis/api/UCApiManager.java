package com.ucloud.library.netanalysis.api;

import com.ucloud.library.netanalysis.BuildConfig;
import com.ucloud.library.netanalysis.api.bean.IpInfoBean;
import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PingDataBean;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.ReportPingBean;
import com.ucloud.library.netanalysis.api.bean.ReportTracerouteBean;
import com.ucloud.library.netanalysis.api.bean.TestIp;
import com.ucloud.library.netanalysis.api.bean.TracerouteDataBean;
import com.ucloud.library.netanalysis.api.bean.UCApiBaseRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.interceptor.BaseInterceptor;
import com.ucloud.library.netanalysis.api.interceptor.EncryptInterceptor;
import com.ucloud.library.netanalysis.api.service.NetAnalysisApiService;
import com.ucloud.library.netanalysis.api.service.NetAnalysisReportApiService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joshua on 2018/10/8 11:16.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiManager {
    private final String TAG = this.getClass().getSimpleName();
    
    public static final long DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final long DEFAULT_WRITE_TIMEOUT = 10 * 1000;
    public static final long DEFAULT_READ_TIMEOUT = 10 * 1000;
    
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private NetAnalysisApiService apiService;
    private NetAnalysisReportApiService reportApiService;
    
    private String token = "fe28de7835c1450a364755ece02f2eba953dd7e4dad727480860b5feae38bf35";
    private String publicKey;
    private String appId;
    
    public UCApiManager() {
        this(BuildConfig.UCLOUD_API_BASE_URL);
    }
    
    public UCApiManager(String baseUrl) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new BaseInterceptor())
                .build();
        
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        Retrofit retrofitWithEncrypt = retrofit.newBuilder()
                .client(okHttpClient.newBuilder()
                        .addNetworkInterceptor(new EncryptInterceptor(publicKey, appId))
                        .build())
                .build();
        
        apiService = retrofit.create(NetAnalysisApiService.class);
        reportApiService = retrofitWithEncrypt.create(NetAnalysisReportApiService.class);
    }
    
    /**
     * 获取移动端公网IP信息
     *
     * @param callback 回调接口 {@link UCApiResponseBean}<{@link PublicIpBean}>
     */
    public void apiGetPublicIpInfo(Callback<UCApiResponseBean<PublicIpBean>> callback) {
        Call<UCApiResponseBean<PublicIpBean>> call = apiService.getPublicIpInfo(new UCApiBaseRequestBean(token));
        call.enqueue(callback);
    }
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param callback 回调接口 {@link UCApiResponseBean}<{@link IpListBean}>
     */
    public void apiGetPingList(Callback<UCApiResponseBean<IpListBean>> callback) {
        Call<UCApiResponseBean<IpListBean>> call = apiService.getPingList(new UCApiBaseRequestBean(token));
        call.enqueue(callback);
    }
    
    /**
     * 上报Ping的结果
     *
     * @param reportAddress 上报接口地址
     * @param pingData      ping结果数据 {@link PingDataBean}
     * @param srcIpInfo     本地IP信息 {@link IpInfoBean}
     * @return response返回     {@link UCApiResponseBean}<{@link MessageBean}>
     * @throws IOException
     */
    public Response<UCApiResponseBean<MessageBean>> apiReportPing(String reportAddress, PingDataBean pingData, IpInfoBean srcIpInfo) throws IOException {
        ReportPingBean report = new ReportPingBean(token, pingData);
        report.setIpInfo(srcIpInfo);
        Call<UCApiResponseBean<MessageBean>> call = reportApiService.reportPing(reportAddress, report);
        return call.execute();
    }
    
    /**
     * 上报Traceroute的结果
     *
     * @param reportAddress  上报接口地址
     * @param tracerouteData traceroute结果数据 {@link TracerouteDataBean}
     * @param srcIpInfo      本地IP信息 {@link IpInfoBean}
     * @return response返回  {@link UCApiResponseBean}<{@link MessageBean}>
     * @throws IOException
     */
    public Response<UCApiResponseBean<MessageBean>> apiReportTraceroute(String reportAddress, TracerouteDataBean tracerouteData, IpInfoBean srcIpInfo) throws IOException {
        ReportTracerouteBean report = new ReportTracerouteBean(token, tracerouteData);
        report.setIpInfo(srcIpInfo);
        Call<UCApiResponseBean<MessageBean>> call = reportApiService.reportTraceroute(reportAddress, report);
        return call.execute();
    }
    
    public void apiGetPublicIpInfo2(Callback<TestIp> callback) {
        Call<TestIp> call = apiService.getPublicIpInfo("https://ipinfo.io/json");
        call.enqueue(callback);
    }
}
