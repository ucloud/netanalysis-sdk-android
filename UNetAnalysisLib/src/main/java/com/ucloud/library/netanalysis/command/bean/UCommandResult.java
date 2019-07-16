package com.ucloud.library.netanalysis.command.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/4 11:03.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public abstract class UCommandResult implements JsonSerializable {
    protected UCommandStatus status;
    
    public UCommandStatus getStatus() {
        return status;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("status", status == null ? null : status.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
