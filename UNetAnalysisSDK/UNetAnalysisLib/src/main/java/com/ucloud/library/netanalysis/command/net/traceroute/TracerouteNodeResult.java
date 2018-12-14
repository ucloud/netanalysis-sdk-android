package com.ucloud.library.netanalysis.command.net.traceroute;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteNodeResult extends UNetCommandResult {
    @SerializedName("hop")
    private int hop;
    @SerializedName("routeIp")
    private String routeIp;
    @SerializedName("isFinalRoute")
    private boolean isFinalRoute;
    @SerializedName("delaies")
    protected List<Float> delaies;
    
    protected TracerouteNodeResult(String targetIp, int hop) {
        super(targetIp);
        this.hop = hop;
        isFinalRoute = false;
        routeIp = "*";
        delaies = new ArrayList<>();
    }
    
    public int getHop() {
        return hop;
    }
    
    public String getRouteIp() {
        return routeIp;
    }
    
    public boolean isFinalRoute() {
        return isFinalRoute;
    }
    
    public List<Float> getDelaies() {
        return delaies;
    }
    
    TracerouteNodeResult setHop(int hop) {
        this.hop = hop;
        return this;
    }
    
    TracerouteNodeResult setRouteIp(String routeIp) {
        this.routeIp = routeIp;
        isFinalRoute = TextUtils.equals(targetIp, routeIp);
        return this;
    }
    
    TracerouteNodeResult setFinalRoute(boolean isFinalRoute) {
        this.isFinalRoute = isFinalRoute;
        return this;
    }
    
    TracerouteNodeResult setStatus(UCommandStatus status) {
        this.status = status;
        return this;
    }
    
    public int averageDelay() {
        int count = 0;
        float total = 0.f;
        for (float delay : delaies) {
            if (delay <= 0.f)
                continue;
            
            count++;
            total += delay;
        }
    
        return Math.round(total / count);
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
