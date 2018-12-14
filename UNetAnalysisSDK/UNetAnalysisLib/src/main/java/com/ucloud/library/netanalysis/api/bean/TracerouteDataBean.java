package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:42.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteDataBean extends NetDataBean {
    @SerializedName("route_info")
    private List<RouteInfoBean> routeInfoList;
    
    public List<RouteInfoBean> getRouteInfoList() {
        return routeInfoList;
    }
    
    public void setRouteInfoList(List<RouteInfoBean> routeInfoList) {
        this.routeInfoList = routeInfoList;
    }
    
    public static class RouteInfoBean {
        @SerializedName("delay")
        private int delay;
        @SerializedName("route_ip")
        private String routeIp;
        
        public int getDelay() {
            return delay;
        }
        
        public void setDelay(int delay) {
            this.delay = delay;
        }
        
        public String getRouteIp() {
            return routeIp;
        }
        
        public void setRouteIp(String routeIp) {
            this.routeIp = routeIp;
        }
        
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
