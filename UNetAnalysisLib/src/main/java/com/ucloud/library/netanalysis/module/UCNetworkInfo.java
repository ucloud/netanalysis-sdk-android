package com.ucloud.library.netanalysis.module;

import android.net.NetworkInfo;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/20 15:11.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCNetworkInfo implements JsonSerializable {
    /**
     * android系统网络信息类：{@link android.net.NetworkInfo}
     */
    private NetworkInfo sysNetInfo;
    /**
     * UNetAnalysisSDK状态集, {@link UCNetStatus}
     */
    private UCNetStatus netStatus;
    /**
     * 信号强度 (dbm)
     */
    private int signalStrength;

    public UCNetworkInfo(NetworkInfo sysNetInfo) {
        this.sysNetInfo = sysNetInfo;
        this.netStatus = UCNetStatus.parseStatusByNetworkInfo(this.sysNetInfo);
    }

    public NetworkInfo getSysNetInfo() {
        return sysNetInfo;
    }

    public UCNetStatus getNetStatus() {
        return netStatus;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void setSysNetInfo(NetworkInfo sysNetInfo) {
        this.sysNetInfo = sysNetInfo;
        this.netStatus = UCNetStatus.parseStatusByNetworkInfo(this.sysNetInfo);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("netStatus", netStatus.name());
            json.put("signalStrength", signalStrength);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
