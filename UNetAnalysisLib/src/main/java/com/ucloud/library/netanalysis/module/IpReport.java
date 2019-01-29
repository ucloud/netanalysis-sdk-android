package com.ucloud.library.netanalysis.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/9/19 15:07.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpReport {
    @SerializedName("IP")
    private String ip;
    @SerializedName("AverageDelay")
    private int averageDelay;
    @SerializedName("PackageLossRate")
    private int packageLossRate;
    @SerializedName("NetStatus")
    private UCNetStatus netStatus;
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public int getAverageDelay() {
        return averageDelay;
    }
    
    public void setAverageDelay(int averageDelay) {
        this.averageDelay = averageDelay;
    }
    
    public int getPackageLossRate() {
        return packageLossRate;
    }
    
    public void setPackageLossRate(int packageLossRate) {
        this.packageLossRate = packageLossRate;
    }
    
    public UCNetStatus getNetStatus() {
        return netStatus;
    }
    
    public void setNetStatus(UCNetStatus netStatus) {
        this.netStatus = netStatus;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
