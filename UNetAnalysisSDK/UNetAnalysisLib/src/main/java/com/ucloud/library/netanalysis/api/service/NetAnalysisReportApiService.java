package com.ucloud.library.netanalysis.api.service;

import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.ReportPingBean;
import com.ucloud.library.netanalysis.api.bean.ReportTracerouteBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by joshua on 2018/12/14 14:19.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface NetAnalysisReportApiService {
    /**
     * 上报Ping的结果
     *
     * @param url     上报接口地址，由于上报地址为动态后台获取，则无法统一使用baseUrl+apiPath的形式
     * @param request 请求体 {@link ReportPingBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST()
    Call<UCApiResponseBean<MessageBean>> reportPing(@Url String url, @Body ReportPingBean request);
    
    /**
     * 上报Traceroute的结果
     *
     * @param url     上报接口地址，由于上报地址为动态后台获取，则无法统一使用baseUrl+apiPath的形式
     * @param request 请求体 {@link ReportTracerouteBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST()
    Call<UCApiResponseBean<MessageBean>> reportTraceroute(@Url String url, @Body ReportTracerouteBean request);
}
