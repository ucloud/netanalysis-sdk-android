package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:47.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteBean extends UCReportBean {
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
        private List<TracerouteDataBean.RouteInfoBean> routeInfoList;
        private List<String> routeList;
        private List<Integer> delayList;
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
            JSONObject json = new JSONObject();
            if (routeList == null || routeList.isEmpty()
                    || delayList == null || delayList.isEmpty()
                    || lossList == null || lossList.isEmpty())
                return json;
            
            JSONArray arrRoute = new JSONArray();
            JSONArray arrDelay = new JSONArray();
            JSONArray arrLoss = new JSONArray();
            
            try {
                for (int i = 0, len = routeList.size(); i < len; i++) {
                    arrRoute.put(routeList.get(i));
                    arrDelay.put(delayList.get(i));
                    arrLoss.put(lossList.get(i));
                }
                json.put("route_list", arrRoute);
                json.put("delay_list", arrDelay);
                json.put("loss_list", arrLoss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            return json;
        }
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("traceroute_data", tracerouteData == null ? JSONObject.NULL : tracerouteData.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
