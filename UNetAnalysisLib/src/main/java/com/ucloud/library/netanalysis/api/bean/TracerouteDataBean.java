package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua on 2018/10/17 16:42.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteDataBean extends NetDataBean {
    private List<RouteInfoBean> routeInfoList;
    
    public List<RouteInfoBean> getRouteInfoList() {
        return routeInfoList;
    }
    
    public void setRouteInfoList(List<RouteInfoBean> routeInfoList) {
        this.routeInfoList = routeInfoList;
    }
    
    public static class RouteInfoBean implements JsonSerializable {
        private int delay;
        private String routeIp;
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
            JSONObject json = new JSONObject();
            try {
                json.put("route_ip", routeIp);
                json.put("delay", delay);
                json.put("loss", loss);
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
        JSONArray jarr = new JSONArray();
        if (routeInfoList != null && !routeInfoList.isEmpty()) {
            for (RouteInfoBean bean : routeInfoList) {
                if (bean == null || bean.toJson().length() == 0)
                    continue;
                
                jarr.put(bean.toJson());
            }
        }
        try {
            json.put("route_info", jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
