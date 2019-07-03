package com.ucloud.library.netanalysis.command.net.ping;


import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PingResult implements JsonSerializable {
    @JsonParam("targetIp")
    private String targetIp;
    @JsonParam("pingPackages")
    private List<SinglePackagePingResult> pingPackages;
    @JsonParam("timestamp")
    private long timestamp;
    
    protected PingResult(String targetIp, long timestamp) {
        this.targetIp = targetIp;
        this.timestamp = timestamp;
        this.pingPackages = new ArrayList<>();
    }
    
    public String getTargetIp() {
        return targetIp;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public List<SinglePackagePingResult> getPingPackages() {
        return pingPackages;
    }
    
    PingResult setPingPackages(List<SinglePackagePingResult> pingPackages) {
        if (pingPackages == null)
            this.pingPackages.clear();
        else
            this.pingPackages.addAll(pingPackages);
        
        return this;
    }
    
    public int averageDelay() {
        int count = 0;
        float total = 0.f;
        for (SinglePackagePingResult pkg : pingPackages) {
            if (pkg == null || pkg.getStatus() != UCommandStatus.CMD_STATUS_SUCCESSFUL || pkg.delaiy == 0.f)
                continue;
            
            count++;
            total += pkg.delaiy;
        }
        
        return Math.round(total / count);
    }
    
    public int lossRate() {
        int loss = 0;
        float total = pingPackages.size();
        for (SinglePackagePingResult pkg : pingPackages) {
            if (pkg == null || pkg.getStatus() != UCommandStatus.CMD_STATUS_SUCCESSFUL || pkg.delaiy == 0.f)
                loss++;
        }
        
        return Math.round(loss / total * 100);
    }
    
    public int accessTTL() {
        for (SinglePackagePingResult pkg : pingPackages) {
            if (pkg == null || pkg.getStatus() != UCommandStatus.CMD_STATUS_SUCCESSFUL || pkg.delaiy == 0.f)
                continue;
            
            return pkg.TTL;
        }
        
        return 0;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        return null;
    }
}
