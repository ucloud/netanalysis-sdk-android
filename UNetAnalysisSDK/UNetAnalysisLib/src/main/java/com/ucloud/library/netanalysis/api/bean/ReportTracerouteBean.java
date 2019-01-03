package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:47.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteBean extends UCReportBean {
    @SerializedName("traceroute_data")
    private ReportTracerouteData tracerouteData;
    
    public ReportTracerouteBean(String appKey, TracerouteDataBean tracerouteData, ReportTracerouteTagBean tag, IpInfoBean ipInfo) {
        super(appKey, "traceroute", tag, ipInfo);
        if (tracerouteData != null)
            this.tracerouteData = new ReportTracerouteData(tracerouteData.getRouteInfoList());
    }
    
    public ReportTracerouteData getTracerouteData() {
        return tracerouteData;
    }
    
    public void setTracerouteData(TracerouteDataBean tracerouteData) {
        if (tracerouteData != null)
            this.tracerouteData = new ReportTracerouteData(tracerouteData.getRouteInfoList());
    }
    
    public static class ReportTracerouteData {
        @SerializedName("route_info")
        private List<TracerouteDataBean.RouteInfoBean> routeInfoList;
        
        public ReportTracerouteData(List<TracerouteDataBean.RouteInfoBean> routeInfoList) {
            this.routeInfoList = routeInfoList;
        }
        
        public List<TracerouteDataBean.RouteInfoBean> getRouteInfoList() {
            return routeInfoList;
        }
        
        public void setRouteInfoList(List<TracerouteDataBean.RouteInfoBean> routeInfoList) {
            this.routeInfoList = routeInfoList;
        }
        
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
