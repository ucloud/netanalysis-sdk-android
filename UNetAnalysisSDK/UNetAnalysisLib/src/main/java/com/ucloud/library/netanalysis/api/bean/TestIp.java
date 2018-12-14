package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;

/**
 * Created by joshua on 2018/10/18 17:39.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TestIp {
    
    /**
     * ip : 107.150.118.71
     * city : Los Angeles
     * region : California
     * country : US
     * loc : 34.0729,-118.2610
     * postal : 90012
     * org : AS135377 UCloud (HK) Holdings Group Limited
     */
    
    private String ip;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String postal;
    private String org;
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getLoc() {
        return loc;
    }
    
    public void setLoc(String loc) {
        this.loc = loc;
    }
    
    public String getPostal() {
        return postal;
    }
    
    public void setPostal(String postal) {
        this.postal = postal;
    }
    
    public String getOrg() {
        return org;
    }
    
    public void setOrg(String org) {
        this.org = org;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    
    public IpInfoBean copy2IpInfoBean() {
        IpInfoBean ip = new IpInfoBean();
        ip.setCity(this.city);
        ip.setCountry(this.country);
        ip.setIp(this.ip);
        ip.setLocation(this.loc);
        ip.setOrg(this.org);
        ip.setRegion(this.region);
        
        return ip;
    }
}
