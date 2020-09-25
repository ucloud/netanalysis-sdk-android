package com.ucloud.library.netanalysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.ucloud.library.netanalysis.api.bean.UCApiBaseRequestBean;
import com.ucloud.library.netanalysis.api.bean.SdkStatus;
import com.ucloud.library.netanalysis.api.http.HttpMethod;
import com.ucloud.library.netanalysis.api.http.Request;
import com.ucloud.library.netanalysis.api.http.Response;
import com.ucloud.library.netanalysis.api.http.UCHttpsClient;
import com.ucloud.library.netanalysis.api.http.UCHttpClient;
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
import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.parser.JsonDeserializer;
import com.ucloud.library.netanalysis.parser.JsonSerializable;
import com.ucloud.library.netanalysis.utils.Encryptor;
import com.ucloud.library.netanalysis.utils.HexFormatter;
import com.ucloud.library.netanalysis.utils.JLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by joshua on 2018/10/8 11:16.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
final class UCApiManager {
    private final String TAG = this.getClass().getSimpleName();
    
    private final String KEY_SP_UUID = "uuid";
    
    private final String packageName;
    private SharedPreferences sharedPreferences;
    
    private UCHttpClient httpClient;
    private UCHttpsClient httpsClient;
    
    private final String appKey;
    private final PublicKey appSecret;
    private String uuid;
    
    UCApiManager(Context context, String appKey, PublicKey appSecret) {
        this.packageName = context.getPackageName();
        this.appKey = appKey;
        this.appSecret = appSecret;
        
        prepareUuid(context);
        
        httpClient = new UCHttpClient();
        httpsClient = new UCHttpsClient();
    }
    
    private class PrepareUuidThread extends Thread {
        private Context context;
        
        public PrepareUuidThread(Context context) {
            super("prepare-uuid");
            this.context = context;
        }
        
        @Override
        public void run() {
            sharedPreferences = context.getSharedPreferences("umqa-sdk", Context.MODE_PRIVATE);
            uuid = sharedPreferences.getString(KEY_SP_UUID, null);
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString().toUpperCase();
                sharedPreferences.edit().putString(KEY_SP_UUID, uuid).apply();
            }
        }
    }
    
    private void prepareUuid(Context context) {
        new PrepareUuidThread(context).start();
    }
    
    Response<UCApiResponseBean<SdkStatus>> apiGetSdkStatus() throws UCHttpException {
        UCApiBaseRequestBean requestBean = new UCApiBaseRequestBean(appKey);
        
        UCHttpClient client = BuildConfig.UCLOUD_API.startsWith("https") ? httpsClient : httpClient;
        return client.execute(new Request.RequestBuilder<JsonSerializable>(BuildConfig.UCLOUD_API, HttpMethod.POST)
                .path("/api/iplist/getsdkstatus/")
                .body(requestBean)
                .build(), new JsonDeserializer<UCApiResponseBean<SdkStatus>>() {
            @Override
            public UCApiResponseBean<SdkStatus> fromJson(String json) throws JSONException {
                UCApiResponseBean<SdkStatus> response = new UCApiResponseBean<>();
                JSONObject jObj = new JSONObject(json);
                JSONObject meta = jObj.optJSONObject("meta");
                if (meta != null) {
                    UCApiResponseBean.MetaBean metaBean = new UCApiResponseBean.MetaBean();
                    metaBean.setCode(meta.getInt("code"));
                    metaBean.setError(meta.optString("error", ""));
                    response.setMeta(metaBean);
                }
                JSONObject data = jObj.optJSONObject("data");
                if (data != null) {
                    SdkStatus status = new SdkStatus();
                    status.setEnabled(data.optInt("enabled", 0));
                    response.setData(status);
                }
                return response;
            }
        });
    }
    
    /**
     * 获取移动端公网IP信息
     *
     * @return 公网IP信息 {@link PublicIpBean}
     * @throws UCHttpException
     */
    Response<PublicIpBean> apiGetPublicIpInfo() throws UCHttpException {
        UCHttpClient client = BuildConfig.UCLOUD_API_IPIP.startsWith("https") ? httpsClient : httpClient;
        return client.execute(new Request.RequestBuilder<JsonSerializable>(BuildConfig.UCLOUD_API_IPIP, HttpMethod.GET)
                .path("/v1/ipip")
                .build(), new JsonDeserializer<PublicIpBean>() {
            @Override
            public PublicIpBean fromJson(String json) throws JSONException {
                PublicIpBean response = new PublicIpBean();
                JSONObject jObj = new JSONObject(json);
                String ret = jObj.optString("ret", "");
                response.setRet(ret);
                JSONObject jData = jObj.optJSONObject("data");
                IpInfoBean bean = new IpInfoBean();
                if (jData != null) {
                    bean.setIp(jData.optString("addr"));
                    bean.setCityName(jData.optString("city_name"));
                    bean.setContinentCode(jData.optString("continent_code"));
                    bean.setCountryCode(jData.optString("country_code"));
                    bean.setCountryName(jData.optString("country_name"));
                    bean.setIspDomain(jData.optString("isp_domain"));
                    bean.setLatitude(jData.optString("latitude"));
                    bean.setLongitude(jData.optString("longitude"));
                    bean.setNetType(jData.optString("net_type"));
                    bean.setOwnerDomain(jData.optString("owner_domain"));
                    bean.setRegionName(jData.optString("region_name"));
                    bean.setTimezone(jData.optString("timezone"));
                    bean.setUtcOffset(jData.optString("utc_offset"));
                }
                response.setIpInfo(bean);
                
                return response;
            }
        });
    }
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param ipInfoBean 终端外网IP信息 {@link IpInfoBean}
     * @return {@link UCApiResponseBean}<{@link IpListBean}>
     * @throws UCHttpException
     */
    Response<UCApiResponseBean<IpListBean>> apiGetPingList(final IpInfoBean ipInfoBean) throws UCHttpException {
        UCGetIpListRequestBean requestBean = new UCGetIpListRequestBean(appKey);
        if (ipInfoBean != null) {
            requestBean.setLongitude(ipInfoBean.getLongitude());
            requestBean.setLatitude(ipInfoBean.getLatitude());
        }
        
        UCHttpClient client = BuildConfig.UCLOUD_API.startsWith("https") ? httpsClient : httpClient;
        return client.execute(new Request.RequestBuilder<JsonSerializable>(BuildConfig.UCLOUD_API, HttpMethod.POST)
                .path("/api/iplist/getpinglist/")
                .body(requestBean)
                .build(), new JsonDeserializer<UCApiResponseBean<IpListBean>>() {
            @Override
            public UCApiResponseBean<IpListBean> fromJson(String json) throws JSONException {
                UCApiResponseBean<IpListBean> response = new UCApiResponseBean<>();
                JSONObject jObj = new JSONObject(json);
                JSONObject meta = jObj.optJSONObject("meta");
                if (meta != null) {
                    UCApiResponseBean.MetaBean metaBean = new UCApiResponseBean.MetaBean();
                    metaBean.setCode(meta.getInt("code"));
                    metaBean.setError(meta.optString("error", ""));
                    response.setMeta(metaBean);
                }
                JSONObject data = jObj.optJSONObject("data");
                if (data != null) {
                    IpListBean ipListBean = new IpListBean();
                    ipListBean.setDomain(data.optString("domain", ""));
                    
                    List<String> url = new ArrayList<>();
                    JSONArray arrUrl = data.optJSONArray("url");
                    if (arrUrl != null) {
                        for (int i = 0, len = arrUrl.length(); i < len; i++) {
                            url.add(arrUrl.optString(i, ""));
                        }
                    }
                    ipListBean.setUrl(url);
                    
                    List<IpListBean.InfoBean> info = new ArrayList<>();
                    JSONArray arrInfo = data.optJSONArray("info");
                    if (arrInfo != null) {
                        for (int i = 0, len = arrInfo.length(); i < len; i++) {
                            JSONObject jInfo = arrInfo.optJSONObject(i);
                            if (jInfo == null)
                                continue;
                            IpListBean.InfoBean bean = new IpListBean.InfoBean();
                            bean.setIp(jInfo.optString("ip", ""));
                            bean.setLocation(jInfo.optString("location", ""));
                            bean.setType(jInfo.optInt("type", 0));
                            bean.setId(jInfo.optInt("id"));
                            info.add(bean);
                        }
                    }
                    ipListBean.setInfo(info);
                    response.setData(ipListBean);
                }
                
                return response;
            }
        });
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
            pingData, int pingStatus, boolean isCustomIp, IpInfoBean srcIpInfo, boolean isManmul,
                                                           UserDefinedData userDefinedData) throws UCHttpException {
        if (TextUtils.isEmpty(reportAddress))
            throw new UCHttpException("URL is empty!");
        
        ReportPingTagBean reportTag = new ReportPingTagBean(packageName, pingData.getDst_ip(), pingData.getTTL());
        reportTag.setCus(isCustomIp);
        ReportPingBean report = new ReportPingBean(appKey, pingData, pingStatus,
                reportTag, srcIpInfo, userDefinedData);
        report.setUuid(uuid);
        report.setTrigger(isManmul ? 1 : 0);
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        UCHttpClient client = reportAddress.startsWith("https") ? httpsClient : httpClient;
        return client.execute(new Request.RequestBuilder<JsonSerializable>(reportAddress, HttpMethod.POST)
                .body(reportEncryptBean)
                .build(), new JsonDeserializer<UCApiResponseBean<MessageBean>>() {
            @Override
            public UCApiResponseBean<MessageBean> fromJson(String json) throws JSONException {
                UCApiResponseBean<MessageBean> response = new UCApiResponseBean<>();
                JSONObject jObj = new JSONObject(json);
                JSONObject meta = jObj.optJSONObject("meta");
                if (meta != null) {
                    UCApiResponseBean.MetaBean metaBean = new UCApiResponseBean.MetaBean();
                    metaBean.setCode(meta.getInt("code"));
                    metaBean.setError(meta.optString("error", ""));
                    response.setMeta(metaBean);
                }
                JSONObject data = jObj.optJSONObject("data");
                if (data != null) {
                    MessageBean message = new MessageBean();
                    message.setMessage(data.optString("message", ""));
                    response.setData(message);
                }
                return response;
            }
        });
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
    Response<UCApiResponseBean<MessageBean>> apiReportTraceroute(String reportAddress, TracerouteDataBean tracerouteData,
                                                                 boolean isCustomIp, IpInfoBean srcIpInfo,
                                                                 boolean isManmul, UserDefinedData userDefinedData) throws UCHttpException {
        if (TextUtils.isEmpty(reportAddress))
            throw new UCHttpException("URL is empty!");
        
        ReportTracerouteTagBean reportTag = new ReportTracerouteTagBean(packageName, tracerouteData.getDst_ip());
        reportTag.setCus(isCustomIp);
        ReportTracerouteBean report = new ReportTracerouteBean(appKey, tracerouteData,
                reportTag
                , srcIpInfo, userDefinedData);
        report.setUuid(uuid);
        report.setTrigger(isManmul ? 1 : 0);
        
        UCReportEncryptBean reportEncryptBean = encryptReportData(report);
        if (reportEncryptBean == null)
            return null;
        
        UCHttpClient client = reportAddress.startsWith("https") ? httpsClient : httpClient;
        return client.execute(new Request.RequestBuilder<JsonSerializable>(reportAddress, HttpMethod.POST)
                .body(reportEncryptBean)
                .build(), new JsonDeserializer<UCApiResponseBean<MessageBean>>() {
            @Override
            public UCApiResponseBean<MessageBean> fromJson(String json) throws JSONException {
                UCApiResponseBean<MessageBean> response = new UCApiResponseBean<>();
                JSONObject jObj = new JSONObject(json);
                JSONObject meta = jObj.optJSONObject("meta");
                if (meta != null) {
                    UCApiResponseBean.MetaBean metaBean = new UCApiResponseBean.MetaBean();
                    metaBean.setCode(meta.getInt("code"));
                    metaBean.setError(meta.optString("error", ""));
                    response.setMeta(metaBean);
                }
                JSONObject data = jObj.optJSONObject("data");
                if (data != null) {
                    MessageBean message = new MessageBean();
                    message.setMessage(data.optString("message", ""));
                    response.setData(message);
                }
                return response;
            }
        });
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
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            JLog.D(TAG, "encrypt report data error: " + e.getMessage());
        }
        
        return null;
    }
    
    private String encryptRSA(String oriData, PublicKey publicKey) throws NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        byte[] srcData = oriData.getBytes(Charset.forName("UTF-8"));
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
