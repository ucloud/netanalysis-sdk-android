package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:27.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpInfoBean {
    /**
     * city : shanghai
     * country : china
     * ip : 192.168.152.12
     * location : string
     * org : string
     * region : shanghai
     */
    
    @SerializedName("city")
    private String city;
    @SerializedName("country")
    private String country;
    @SerializedName("ip")
    private String ip;
    @SerializedName("location")
    private String location;
    @SerializedName("org")
    private String org;
    @SerializedName("region")
    private String region;
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getOrg() {
        return org;
    }
    
    public void setOrg(String org) {
        this.org = org;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
