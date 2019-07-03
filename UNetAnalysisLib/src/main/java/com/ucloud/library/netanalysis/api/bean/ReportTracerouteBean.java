package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;
import com.ucloud.library.netanalysis.module.UserDefinedData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:47.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteBean extends UCReportBean {
    @JsonParam("traceroute_data")
    private ReportTracerouteData tracerouteData;
    
    public ReportTracerouteBean(String appKey, TracerouteDataBean tracerouteData, ReportTracerouteTagBean tag, IpInfoBean ipInfo, UserDefinedData userDefinedData) {
        super(appKey, "traceroute", tag, ipInfo, userDefinedData);
        if (tracerouteData != null) {
            this.timestamp = tracerouteData.timestamp;
            this.tracerouteData = new ReportTracerouteData(tracerouteData.getRouteInfoList());
        }
    }
    
    public ReportTracerouteData getTracerouteData() {
        return tracerouteData;
    }
    
    public void setTracerouteData(TracerouteDataBean tracerouteData) {
        if (tracerouteData != null)
            this.tracerouteData = new ReportTracerouteData(tracerouteData.getRouteInfoList());
    }

    public static class ReportTracerouteData implements JsonSerializable {
        @JsonParam("route_info")
        private transient List<TracerouteDataBean.RouteInfoBean> routeInfoList;
        @JsonParam("route_list")
        private List<String> routeList;
        @JsonParam("delay_list")
        private List<Integer> delayList;
        @JsonParam("loss_list")
        private List<Integer> lossList;
        
        public ReportTracerouteData(List<TracerouteDataBean.RouteInfoBean> routeInfoList) {
            this.routeInfoList = routeInfoList;
            makeReportData();
        }
        
        private void makeReportData() {
            routeList = new ArrayList<>();
            delayList = new ArrayList<>();
            lossList = new ArrayList<>();
            if (routeInfoList == null || routeInfoList.isEmpty())
                return;
            
            for (int i = 0, len = routeInfoList.size(); i < len; i++) {
                TracerouteDataBean.RouteInfoBean route = routeInfoList.get(i);
                routeList.add(route.getRouteIp());
                delayList.add(route.getDelay());
                lossList.add(route.getLoss());
            }
        }
        
        public List<TracerouteDataBean.RouteInfoBean> getRouteInfoList() {
            return routeInfoList;
        }
        
        public void setRouteInfoList(List<TracerouteDataBean.RouteInfoBean> routeInfoList) {
            this.routeInfoList = routeInfoList;
            makeReportData();
        }
        
        @Override
        public String toString() {
            return toJson().toString();
        }

        @Override
        public JSONObject toJson() {
            return null;
        }
    }
}
