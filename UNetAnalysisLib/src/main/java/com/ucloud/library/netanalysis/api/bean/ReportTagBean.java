package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.UCNetAnalysisManager;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTagBean {
    @SerializedName("app_id")
    protected String appId;
    @SerializedName("platform")
    protected int platform = 0;
    @SerializedName("s_ver")
    protected final String sdkVersion = UCNetAnalysisManager.SDK_VERSION;
    @SerializedName("cus")
    protected int cus = 0;
    @SerializedName("tz")
    protected String timezone;
    
    protected ReportTagBean(String appId) {
        this.appId = appId;
        try {
            this.timezone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.getDefault());
            this.timezone = this.timezone.replace("GMT", "").replace(":", "");
        } catch (Exception e) {
            this.timezone = null;
        }
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public int getPlatform() {
        return platform;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public boolean isCustomIp() {
        return cus > 0;
    }
    
    public void setCus(boolean isCustomIp) {
        this.cus = isCustomIp ? 1 : 0;
    }
    
    protected String makeReportString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("app_id=%s", appId));
        sb.append(String.format(",platform=%d", platform));
        sb.append(String.format(",s_ver=%s", sdkVersion));
        sb.append(String.format(",cus=%d", cus));
        sb.append(String.format(",tz=%s", (timezone == null ? "" : timezone)));
        return sb.toString();
    }
    
}
