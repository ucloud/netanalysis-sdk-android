package com.ucloud.library.netanalysis.api.service;

import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.TestIp;
import com.ucloud.library.netanalysis.api.bean.UCApiBaseRequestBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;

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
     * @param request 请求体 {@link UCApiBaseRequestBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST()
    Call<UCApiResponseBean<PublicIpBean>> getPublicIpInfo(@Body UCApiBaseRequestBean request);
    
    /**
     * 获取UCloud需要监测的IP列表，以及上报服务器列表
     *
     * @param request 请求体 {@link UCApiBaseRequestBean}
     * @return
     */
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @POST("report/getpinglist/")
    Call<UCApiResponseBean<IpListBean>> getPingList(@Body UCApiBaseRequestBean request);
    
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: */*"})
    @GET()
    Call<TestIp> getPublicIpInfo(@Url String url);
}
