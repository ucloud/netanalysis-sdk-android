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
    @SerializedName("singleNodeList")
    private List<SingleNodeResult> singleNodeList;
    
    protected TracerouteNodeResult(String targetIp, int hop, List<SingleNodeResult> singleNodeList) {
        super(targetIp);
        this.hop = hop;
        isFinalRoute = false;
        routeIp = "*";
        setSingleNodeList(singleNodeList);
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
    
    public List<SingleNodeResult> getSingleNodeList() {
        return singleNodeList;
    }
    
    void setSingleNodeList(List<SingleNodeResult> singleNodeList) {
        this.singleNodeList = singleNodeList;
        if (this.singleNodeList == null)
            return;
        
        for (SingleNodeResult node : this.singleNodeList) {
            if (!TextUtils.equals("*", node.getRouteIp())) {
                setRouteIp(node.getRouteIp());
                break;
            }
        }
    }
    
    public int averageDelay() {
        if (singleNodeList == null || singleNodeList.isEmpty())
            return 0;
        
        int count = 0;
        float total = 0.f;
        for (SingleNodeResult node : singleNodeList) {
            if (node == null)
                continue;
            if (node.delay <= 0.f)
                continue;
            
            count++;
            total += node.delay;
        }
    
        return Math.round(total / count);
    }
    
    public int lossRate() {
        if (singleNodeList == null || singleNodeList.isEmpty())
            return 100;
        
        int loss = 0;
        float total = singleNodeList.size();
        for (SingleNodeResult node : singleNodeList) {
            if (node == null || node.getStatus() != UCommandStatus.CMD_STATUS_SUCCESSFUL || node.delay == 0.f)
                loss++;
        }
        
        return Math.round(loss / total * 100);
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
