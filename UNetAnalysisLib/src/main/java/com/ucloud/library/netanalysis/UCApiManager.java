package com.ucloud.library.netanalysis;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.ucloud.library.netanalysis.api.bean.IpInfoBean;
import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PingDataBean;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.ReportPingBean;
import com.ucloud.library.netanalysis.api.bean.ReportPingTagBean;
import com.ucloud.library.netanalysis.api.bean.ReportTracerouteBean;
import com.ucloud.library.netanalysis.api.bean.ReportTracerouteTagBean;
import com.ucloud.library.netanalysis.api.bean.TracerouteDataBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.bean.UCGetIpListRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCReportBean;
import com.ucloud.library.netanalysis.api.bean.UCReportEncryptBean;
import com.ucloud.library.netanalysis.api.interceptor.UCInterceptor;
import com.ucloud.library.netanalysis.api.service.NetAnalysisApiService;
import com.ucloud.library.netanalysis.module.UserDefinedData;
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
final class UCApiManager {
    private final String TAG = this.getClass().getSimpleName();
    
    public static final long DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    public static final long DEFAULT_WRITE_TIMEOUT = 20 * 1000;
    public static final long DEFAULT_READ_TIMEOUT = 20 * 1000;
    
    private Context context;
    private NetAnalysisApiService apiService;
    
    private String appKey;
    private String appSecret;
    
    protected UCApiManager(Context context, String appKey, String appSecret) {
        this.context = context;
        this.appKey = appKey;
        this.appSecret = appSecret;
        
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new UCInterceptor())
                .build();
        
        Retrofit retrofit = new Retrofit.Builder()
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
    void apiGetPublicIpInfo(Callback<PublicIpBean> callback) {
        Call<PublicIpBean> call = apiService.getPublicIpInfo(BuildConfig.UCLOUD_API_IPIP);
        call.enqueue(callback);
    }
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param ipInfoBean 终端外网IP信息 {@link IpInfoBean}
     * @param callback   回调接口 {@link UCApiResponseBean}<{@link IpListBean}>
     */
    void apiGetPingList(IpInfoBean ipInfoBean, Callback<UCApiResponseBean<IpListBean>> callback) {
        UCGetIpListRequestBean requestBean = new UCGetIpListRequestBean(appKey);
        if (ipInfoBean != null) {
            requestBean.setLongitude(ipInfoBean.getLongitude());
            requestBean.setLatitude(ipInfoBean.getLatitude());
        }
        Call<UCApiResponseBean<IpListBean>> call = apiService.getPingList(requestBean);
        call.enqueue(callback);
    }
    
    /**
     * 上报Ping的结果
     * <p>
     *
     * @param reportAddress   上报接口地址
     * @param pingData        ping结果数据 { @link PingDataBean}
     * @param srcIpInfo       本地IP信息 {@link IpInfoBean}
     * @param userDefinedData 用户自定义信息 {@link UserDefinedData}
     * @return response返回 {@link UCApiResponseBean}<{@link MessageBean}>
     * @throws IOException
     */
    Response<UCApiResponseBean<MessageBean>> apiReportPing(String reportAddress, PingDataBean
            pingData, int pingStatus, boolean isCustomIp,
                                                           IpInfoBean srcIpInfo, UserDefinedData userDefinedData) throws IOException {
        ReportPingTagBean reportTag = new ReportPingTagBean(context.getPackageName(), pingData.getDst_ip(), pingData.getTTL());
        reportTag.setCus(isCustomIp);
        ReportPingBean report = new ReportPingBean(appKey, pingData, pingStatus,
                reportTag, srcIpInfo, userDefinedData);
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        Call<UCApiResponseBean<MessageBean>> call = apiService.reportPing(reportAddress, reportEncryptBean);
        return call.execute();
    }
    
    /**
     * 上报Traceroute的结果
     * <p>
     *
     * @param reportAddress   上报接口地址
     * @param tracerouteData  traceroute结果数据 {@link TracerouteDataBean}
     * @param srcIpInfo       本地IP信息 {@link IpInfoBean}
     * @param userDefinedData 用户自定义信息{@link UserDefinedData}
     * @return response返回  {@link UCApiResponseBean}<{@link MessageBean}>
     * @throws IOException
     */
    Response<UCApiResponseBean<MessageBean>> apiReportTraceroute(String reportAddress, TracerouteDataBean tracerouteData, boolean isCustomIp,
                                                                 IpInfoBean srcIpInfo, UserDefinedData userDefinedData) throws IOException {
        ReportTracerouteTagBean reportTag = new ReportTracerouteTagBean(context.getPackageName(), tracerouteData.getDst_ip());
        reportTag.setCus(isCustomIp);
        ReportTracerouteBean report = new ReportTracerouteBean(appKey, tracerouteData,
                reportTag
                , srcIpInfo, userDefinedData);
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        Call<UCApiResponseBean<MessageBean>> call = apiService.reportTraceroute(reportAddress, reportEncryptBean);
        return call.execute();
    }
    
    private static final int RSA_CRYPT_SRC_LIMIT = 128;
    
    private UCReportEncryptBean encryptReportData(UCReportBean reportBean) {
        if (reportBean == null)
            return null;
        
        String oriTag = reportBean.getTag();
        String oriIpInfo = reportBean.getIpInfo();
        UserDefinedData oriUserDefined = reportBean.getUserDefinedData();
        String oriStrUserDefined = oriUserDefined == null ? "" : oriUserDefined.toString();
        
        if (TextUtils.isEmpty(oriTag) || TextUtils.isEmpty(oriIpInfo))
            return null;
        
        UCReportEncryptBean encryptBean = new UCReportEncryptBean("");
        
        try {
            reportBean.setTag(encryptRSA(oriTag, appSecret));
            reportBean.setIpInfo(encryptRSA(oriIpInfo, appSecret));
            reportBean.setUserDefinedStr(TextUtils.isEmpty(oriStrUserDefined) ? "" : encryptRSA(oriStrUserDefined, appSecret));
            
            encryptBean.setData(Base64.encodeToString(reportBean.toString().getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));
            return encryptBean;
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
        }
        
        return null;
    }
    
    private String encryptRSA(String oriData, String key) throws
            InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        byte[] srcData = oriData.getBytes(Charset.forName("UTF-8"));
        PublicKey publicKey = Encryptor.getPublicKey(key);
        byte[] cryptArr = null;
        int len = srcData.length;
        for (int i = 0, count = (int) Math.ceil(len * 1.f / (RSA_CRYPT_SRC_LIMIT - 11)); i < count; i++) {
            int pkgLen = i == (count - 1) ? (len - i * (RSA_CRYPT_SRC_LIMIT - 11)) : (RSA_CRYPT_SRC_LIMIT - 11);
            byte[] src = new byte[pkgLen];
            System.arraycopy(srcData, i * (RSA_CRYPT_SRC_LIMIT - 11), src, 0, pkgLen);
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
        
        return HexFormatter.formatByteArray2HexString(cryptArr, false);
    }
}
