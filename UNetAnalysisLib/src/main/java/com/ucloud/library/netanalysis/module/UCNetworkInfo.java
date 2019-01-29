package com.ucloud.library.netanalysis.module;

import android.net.NetworkInfo;

import com.google.gson.JsonObject;

/**
 * Created by joshua on 2018/9/20 15:11.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCNetworkInfo {
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
        JsonObject json = new JsonObject();
        json.addProperty("netStatus", netStatus.name());
        json.addProperty("signalStrength", signalStrength);
    
        return json.toString();
    }
}
