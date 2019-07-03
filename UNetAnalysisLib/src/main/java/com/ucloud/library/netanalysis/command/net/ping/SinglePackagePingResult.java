package com.ucloud.library.netanalysis.command.net.ping;


import com.google.gson.Gson;
import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandResult;

import org.json.JSONObject;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class SinglePackagePingResult extends UNetCommandResult implements JsonSerializable {
    @JsonParam("delay")
    protected float delaiy;
    @JsonParam("TTL")
    protected int TTL;
    
    protected SinglePackagePingResult(String targetIp) {
        super(targetIp);
        delaiy = 0.f;
    }
    
    SinglePackagePingResult setStatus(UCommandStatus status) {
        this.status = status;
        return this;
    }
    
    public float getDelaiy() {
        return delaiy;
    }
    
    public SinglePackagePingResult setDelaiy(float delaiy) {
        this.delaiy = delaiy;
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
        new Gson().toJson(this);
        return null;
    }
}
