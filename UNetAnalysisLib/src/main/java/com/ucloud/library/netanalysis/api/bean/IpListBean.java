package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joshua on 2018/10/16 14:19.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpListBean {
    @SerializedName("info")
    private List<InfoBean> info;
    @SerializedName("url")
    private List<String> url;
    
    public static class InfoBean {
        @SerializedName("location")
        private String location;
        @SerializedName("ip")
        private String ip;
        
        public String getLocation() {
            return location;
        }
        
        public void setLocation(String location) {
            this.location = location;
        }
        
        public String getIp() {
            return ip;
        }
        
        public void setIp(String ip) {
            this.ip = ip;
        }
        
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
    
    public List<InfoBean> getInfo() {
        return info;
    }
    
    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }
    
    public List<String> getUrl() {
        return url;
    }
    
    public void setUrl(List<String> url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
