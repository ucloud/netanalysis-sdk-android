package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:42.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteDataBean extends NetDataBean {
    @JsonParam("route_info")
    private List<RouteInfoBean> routeInfoList;
    
    public List<RouteInfoBean> getRouteInfoList() {
        return routeInfoList;
    }
    
    public void setRouteInfoList(List<RouteInfoBean> routeInfoList) {
        this.routeInfoList = routeInfoList;
    }

    public static class RouteInfoBean implements JsonSerializable {
        @JsonParam("delay")
        private int delay;
        @JsonParam("route_ip")
        private String routeIp;
        @JsonParam("loss")
        private int loss;
        
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
    
        public int getLoss() {
            return loss;
        }
    
        public void setLoss(int loss) {
            this.loss = loss;
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
