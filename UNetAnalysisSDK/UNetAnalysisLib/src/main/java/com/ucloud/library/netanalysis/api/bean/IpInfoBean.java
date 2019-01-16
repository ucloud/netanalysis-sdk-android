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
    @SerializedName("cityName")
    private String city_name;
    @SerializedName("continentCode")
    private String continentCode;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("ispDomain")
    private String isp_domain;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("ownerDomain")
    private String owner_domain;
    @SerializedName("region_name")
    private String regionName;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("utc_offset")
    private String utcOffset;
    @SerializedName("net_type")
    private String netType;
    
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
    
    public String getContinentCode() {
        return continentCode;
    }
    
    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
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
    
    public String getRegionName() {
        return regionName;
    }
    
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getUtcOffset() {
        return utcOffset;
    }
    
    public void setUtcOffset(String utcOffset) {
        this.utcOffset = utcOffset;
    }
    
    public String getNetType() {
        return netType;
    }
    
    public void setNetType(String netType) {
        this.netType = netType;
    }
    
    protected String makeReportString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("ip=%s", ip));
        sb.append(String.format(",region=%s", regionName));
        sb.append(String.format(",country=%s", countryName));
        sb.append(String.format(",city=%s", city_name));
        sb.append(String.format(",isp=%s", isp_domain));
        sb.append(String.format(",lat=%s", longitude));
        sb.append(String.format(",lat=%s", latitude));
        sb.append(String.format(",owner=%s", owner_domain));
        sb.append(String.format(",net_type=%s", netType));
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
