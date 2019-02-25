package com.ucloud.library.netanalysis.command.net.ping;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandResult;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class SinglePackagePingResult extends UNetCommandResult {
    @SerializedName("delay")
    protected float delaiy;
    @SerializedName("TTL")
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
        return new Gson().toJson(this);
    }
}
