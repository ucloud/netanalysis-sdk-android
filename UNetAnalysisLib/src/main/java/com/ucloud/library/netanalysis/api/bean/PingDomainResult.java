package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.ping.PingResult;

/**
 * Created by joshua on 2019/5/30 14:30.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PingDomainResult {
    @SerializedName("PingResult")
    private PingResult pingResult;
    @SerializedName("CommandStatus")
    private UCommandStatus status;
    
    public PingDomainResult(PingResult pingResult, UCommandStatus status) {
        this.pingResult = pingResult;
        this.status = status;
    }
    
    public PingResult getPingResult() {
        return pingResult;
    }
    
    public void setPingResult(PingResult pingResult) {
        this.pingResult = pingResult;
    }
    
    public UCommandStatus getStatus() {
        return status;
    }
    
    public void setStatus(UCommandStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
