package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:38.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PingDataBean extends NetDataBean{
    @SerializedName("TTL")
    private int TTL;
    @SerializedName("delay")
    private int delay;
    @SerializedName("loss")
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
}
