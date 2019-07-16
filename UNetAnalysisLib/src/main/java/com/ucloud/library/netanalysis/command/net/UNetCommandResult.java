package com.ucloud.library.netanalysis.command.net;

import com.ucloud.library.netanalysis.command.bean.UCommandResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UNetCommandResult extends UCommandResult {
    protected String targetIp;
    
    protected UNetCommandResult(String targetIp) {
        this.targetIp = targetIp;
    }
    
    public String getTargetIp() {
        return targetIp;
    }
    
    protected UNetCommandResult setTargetIp(String targetIp) {
        this.targetIp = targetIp;
        return this;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("targetIp", targetIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
