package com.ucloud.library.netanalysis.command.net.traceroute;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandResult;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class SingleNodeResult extends UNetCommandResult {
    @SerializedName("hop")
    private int hop;
    @SerializedName("routeIp")
    private String routeIp;
    @SerializedName("isFinalRoute")
    private boolean isFinalRoute;
    @SerializedName("delay")
    protected float delay;
    
    protected SingleNodeResult(String targetIp, int hop) {
        super(targetIp);
        this.hop = hop;
        isFinalRoute = false;
        routeIp = "*";
        delay = 0.f;
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
    
    public void setDelay(float delay) {
        this.delay = delay;
    }
    
    public float getDelay() {
        return delay;
    }
    
    SingleNodeResult setHop(int hop) {
        this.hop = hop;
        return this;
    }
    
    SingleNodeResult setRouteIp(String routeIp) {
        this.routeIp = routeIp;
        isFinalRoute = TextUtils.equals(targetIp, routeIp);
        return this;
    }
    
    SingleNodeResult setFinalRoute(boolean isFinalRoute) {
        this.isFinalRoute = isFinalRoute;
        return this;
    }
    
    SingleNodeResult setStatus(UCommandStatus status) {
        this.status = status;
        return this;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
