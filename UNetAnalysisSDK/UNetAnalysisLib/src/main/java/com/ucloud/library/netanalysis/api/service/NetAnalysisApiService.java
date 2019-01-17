package com.ucloud.library.netanalysis.api.service;

import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.UCApiBaseRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.bean.UCGetIpListRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCReportEncryptBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by joshua on 2018/10/11 18:19.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface NetAnalysisApiService {
    /**
     * 获取移动端公网IP信息
     *
     * @param url 接口地址
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @GET()
    Call<PublicIpBean> getPublicIpInfo(@Url String url);
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param request 请求参数 {@link UCGetIpListRequestBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST("iplist/getpinglist/")
    Call<UCApiResponseBean<IpListBean>> getPingList(@Body UCGetIpListRequestBean request);
    
    /**
     * 上报Ping的结果
     *
     * @param url     上报接口地址，由于上报地址为动态后台获取，则无法统一使用baseUrl+apiPath的形式
     * @param request 请求体 {@link UCReportEncryptBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST()
    Call<UCApiResponseBean<MessageBean>> reportPing(@Url String url, @Body UCReportEncryptBean request);
    
    /**
     * 上报Traceroute的结果
     *
     * @param url     上报接口地址，由于上报地址为动态后台获取，则无法统一使用baseUrl+apiPath的形式
     * @param request 请求体 {@link UCReportEncryptBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST()
    Call<UCApiResponseBean<MessageBean>> reportTraceroute(@Url String url, @Body UCReportEncryptBean request);
}
