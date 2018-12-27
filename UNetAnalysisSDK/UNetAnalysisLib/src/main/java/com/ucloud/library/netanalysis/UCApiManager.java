package com.ucloud.library.netanalysis;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.ucloud.library.netanalysis.api.bean.IpInfoBean;
import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PingDataBean;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.ReportPingBean;
import com.ucloud.library.netanalysis.api.bean.ReportTagBean;
import com.ucloud.library.netanalysis.api.bean.ReportTracerouteBean;
import com.ucloud.library.netanalysis.api.bean.TracerouteDataBean;
import com.ucloud.library.netanalysis.api.bean.UCApiBaseRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.bean.UCReportBean;
import com.ucloud.library.netanalysis.api.bean.UCReportEncryptBean;
import com.ucloud.library.netanalysis.api.interceptor.BaseInterceptor;
import com.ucloud.library.netanalysis.api.service.NetAnalysisApiService;
import com.ucloud.library.netanalysis.utils.Encryptor;
import com.ucloud.library.netanalysis.utils.HexFormatter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
class UCApiManager {
    private final String TAG = this.getClass().getSimpleName();
    
    public static final long DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final long DEFAULT_WRITE_TIMEOUT = 10 * 1000;
    public static final long DEFAULT_READ_TIMEOUT = 10 * 1000;
    
    private Context context;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private NetAnalysisApiService apiService;
    
    private String appKey;
    private String appSecret;
    
    protected UCApiManager(Context context, String appKey, String appSecret) {
        this.context = context;
        this.appKey = appKey;
        this.appSecret = appSecret;
        
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new BaseInterceptor())
                .build();
        
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.UCLOUD_API_IP_LIST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(NetAnalysisApiService.class);
    }
    
    /**
     * 获取移动端公网IP信息
     *
     * @param callback 回调接口 {@link PublicIpBean}
     */
    public void apiGetPublicIpInfo(Callback<PublicIpBean> callback) {
        Call<PublicIpBean> call = apiService.getPublicIpInfo(BuildConfig.UCLOUD_API_IPIP);
        call.enqueue(callback);
    }
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param callback 回调接口 {@link UCApiResponseBean}<{@link IpListBean}>
     */
    public void apiGetPingList(Callback<UCApiResponseBean<IpListBean>> callback) {
        Call<UCApiResponseBean<IpListBean>> call = apiService.getPingList(new UCApiBaseRequestBean(appKey));
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
        ReportPingBean report = new ReportPingBean(pingData,
                ReportTagBean.generate(appKey, context.getPackageName(), srcIpInfo));
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        Call<UCApiResponseBean<MessageBean>> call = apiService.reportPing(reportAddress, reportEncryptBean);
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
        ReportTracerouteBean report = new ReportTracerouteBean(tracerouteData,
                ReportTagBean.generate(appKey, context.getPackageName(), srcIpInfo));
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        Call<UCApiResponseBean<MessageBean>> call = apiService.reportTraceroute(reportAddress, reportEncryptBean);
        return call.execute();
    }
    
    private static final int CRYPT_SRC_LIMIT = 117;
    
    private UCReportEncryptBean encryptReportData(UCReportBean reportBean) {
        if (reportBean == null)
            return null;
        
        String oriData = reportBean.toString();
        if (TextUtils.isEmpty(oriData))
            return null;
        
        UCReportEncryptBean encryptBean = new UCReportEncryptBean(appKey);
        try {
            byte[] srcData = oriData.getBytes(Charset.forName("UTF-8"));
            PublicKey publicKey = Encryptor.getPublicKey(Base64.decode(appSecret.getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));
            byte[] cryptArr = null;
            int len = srcData.length;
            for (int i = 0, count = (int) Math.ceil(len * 1.f / CRYPT_SRC_LIMIT); i < count; i++) {
                int pkgLen = i == (count - 1) ? (len - i * CRYPT_SRC_LIMIT) : CRYPT_SRC_LIMIT;
                byte[] src = new byte[pkgLen];
                System.arraycopy(srcData, 0 * CRYPT_SRC_LIMIT, src, 0, pkgLen);
                byte[] tmp = Encryptor.encryptRSA(src, publicKey);
                if (cryptArr == null) {
                    cryptArr = Arrays.copyOf(tmp, tmp.length);
                } else {
                    byte[] buff = new byte[cryptArr.length + tmp.length];
                    System.arraycopy(cryptArr, 0, buff, 0, cryptArr.length);
                    System.arraycopy(tmp, 0, buff, cryptArr.length, tmp.length);
                    cryptArr = buff;
                }
            }
            
            encryptBean.setData(HexFormatter.formatByteArray2HexString(cryptArr, false));
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
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            return encryptBean;
        }
    }
}
