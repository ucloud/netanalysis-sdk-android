package com.ucloud.library.netanalysis.command.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/9/4 11:03.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public abstract class UCommandResult {
    @SerializedName("status")
    protected UCommandStatus status;
    
    public UCommandStatus getStatus() {
        return status;
    }
}
