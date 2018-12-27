package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:27.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpInfoBean {
    @SerializedName("addr")
    private String ip;
    @SerializedName("city_name")
    private String city_name;
    @SerializedName("continent_code")
    private String continent_code;
    @SerializedName("country_code")
    private String country_code;
    @SerializedName("country_name")
    private String country_name;
    @SerializedName("isp_domain")
    private String isp_domain;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("owner_domain")
    private String owner_domain;
    @SerializedName("region_name")
    private String region_name;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("utc_offset")
    private String utc_offset;
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getCity_name() {
        return city_name;
    }
    
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
    
    public String getContinent_code() {
        return continent_code;
    }
    
    public void setContinent_code(String continent_code) {
        this.continent_code = continent_code;
    }
    
    public String getCountry_code() {
        return country_code;
    }
    
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
    
    public String getCountry_name() {
        return country_name;
    }
    
    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
    
    public String getIsp_domain() {
        return isp_domain;
    }
    
    public void setIsp_domain(String isp_domain) {
        this.isp_domain = isp_domain;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String getOwner_domain() {
        return owner_domain;
    }
    
    public void setOwner_domain(String owner_domain) {
        this.owner_domain = owner_domain;
    }
    
    public String getRegion_name() {
        return region_name;
    }
    
    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getUtc_offset() {
        return utc_offset;
    }
    
    public void setUtc_offset(String utc_offset) {
        this.utc_offset = utc_offset;
    }
    
    protected void copy(IpInfoBean ipInfo) {
        if (ipInfo == null)
            return;
        ip = ipInfo.ip;
        city_name = ipInfo.city_name;
        continent_code = ipInfo.continent_code;
        country_code = ipInfo.country_code;
        country_name = ipInfo.country_name;
        isp_domain = ipInfo.isp_domain;
        latitude = ipInfo.latitude;
        longitude = ipInfo.longitude;
        owner_domain = ipInfo.owner_domain;
        region_name = ipInfo.region_name;
        timezone = ipInfo.timezone;
        utc_offset = ipInfo.utc_offset;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
