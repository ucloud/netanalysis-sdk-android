package com.ucloud.library.netanalysis.command.net.ping;


import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class SinglePackagePingResult extends UNetCommandResult {
    protected float delay;
    protected int TTL;
    
    protected SinglePackagePingResult(String targetIp) {
        super(targetIp);
        delay = 0.f;
    }
    
    SinglePackagePingResult setStatus(UCommandStatus status) {
        this.status = status;
        return this;
    }
    
    public float getDelay() {
        return delay;
    }
    
    public SinglePackagePingResult setDelay(float delay) {
        this.delay = delay;
        return this;
    }
    
    public int getTTL() {
        return TTL;
    }
    
    public SinglePackagePingResult setTTL(int TTL) {
        this.TTL = TTL;
        return this;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("delay", delay);
            json.put("TTL", TTL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
