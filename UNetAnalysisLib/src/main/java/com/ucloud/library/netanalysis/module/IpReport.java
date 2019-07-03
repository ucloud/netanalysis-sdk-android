package com.ucloud.library.netanalysis.module;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/19 15:07.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpReport implements JsonSerializable {
    @JsonParam("IP")
    private String ip;
    @JsonParam("AverageDelay")
    private int averageDelay;
    @JsonParam("PackageLossRate")
    private int packageLossRate;
    @JsonParam("NetStatus")
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
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("IP", ip);
            json.put("AverageDelay", averageDelay);
            json.put("PackageLossRate", packageLossRate);
            json.put("NetStatus", netStatus.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
