package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 16:27.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpInfoBean implements JsonSerializable {
    @JsonParam("addr")
    private String ip;
    @JsonParam("city_name")
    private String cityName;
    @JsonParam("continent_code")
    private String continentCode;
    @JsonParam("country_code")
    private String countryCode;
    @JsonParam("country_name")
    private String countryName;
    @JsonParam("isp_domain")
    private String ispDomain;
    @JsonParam("latitude")
    private String latitude;
    @JsonParam("longitude")
    private String longitude;
    @JsonParam("owner_domain")
    private String ownerDomain;
    @JsonParam("region_name")
    private String regionName;
    @JsonParam("timezone")
    private String timezone;
    @JsonParam("utc_offset")
    private String utcOffset;
    @JsonParam("net_type")
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
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("addr", ip);
            json.put("region_name", regionName);
            json.put("country_name", countryName);
            json.put("city_name", cityName);
            json.put("isp_domain", ispDomain);
            json.put("longitude", longitude);
            json.put("latitude", latitude);
            json.put("owner_domain", ownerDomain);
            json.put("net_type", netType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
