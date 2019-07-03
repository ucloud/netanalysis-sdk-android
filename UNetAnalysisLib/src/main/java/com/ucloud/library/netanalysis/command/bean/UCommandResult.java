package com.ucloud.library.netanalysis.command.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;

/**
 * Created by joshua on 2018/9/4 11:03.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public abstract class UCommandResult {
    @JsonParam("status")
    protected UCommandStatus status;
    
    public UCommandStatus getStatus() {
        return status;
    }
}
