package com.ucloud.library.netanalysis.api.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 16:38.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PingDataBean extends NetDataBean {
    private int TTL;
    private int delay;
    private int loss;
    
    public int getTTL() {
        return TTL;
    }
    
    public void setTTL(int TTL) {
        this.TTL = TTL;
    }
    
    public int getDelay() {
        return delay;
    }
    
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    public int getLoss() {
        return loss;
    }
    
    public void setLoss(int loss) {
        this.loss = loss;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        
        try {
            json.put("TTL", TTL);
            json.put("delay", delay);
            json.put("loss", loss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
