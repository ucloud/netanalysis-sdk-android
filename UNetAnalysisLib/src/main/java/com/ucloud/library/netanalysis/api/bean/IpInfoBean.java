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
    private String cityName;
    @SerializedName("continentCode")
    private String continentCode;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("ispDomain")
    private String ispDomain;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("ownerDomain")
    private String ownerDomain;
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
    
    public String getCityName() {
        return cityName;
    }
    
    public void setCityName(String cityName) {
        this.cityName = cityName;
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
    
    public String getIspDomain() {
        return ispDomain;
    }
    
    public void setIspDomain(String ispDomain) {
        this.ispDomain = ispDomain;
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
    
    public String getOwnerDomain() {
        return ownerDomain;
    }
    
    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain = ownerDomain;
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
        sb.append(String.format("ip=%s", (ip == null ? "" : ip)));
        sb.append(String.format(",region=%s", (regionName == null ? "" : regionName)));
        sb.append(String.format(",country=%s", (countryName == null ? "" : countryName)));
        sb.append(String.format(",city=%s", (cityName == null ? "" : cityName)));
        sb.append(String.format(",isp=%s", (ispDomain == null ? "" : ispDomain)));
        sb.append(String.format(",lon=%s", (longitude == null ? "" : longitude)));
        sb.append(String.format(",lat=%s", (latitude == null ? "" : latitude)));
        sb.append(String.format(",owner=%s", (ownerDomain == null ? "" : ownerDomain)));
        sb.append(String.format(",net_type=%s", (netType == null ? "" : netType)));
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
