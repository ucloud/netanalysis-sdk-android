package com.ucloud.library.netanalysis.command.net;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.command.bean.UCommandResult;

/**
 * Created by joshua on 2018/9/5 18:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UNetCommandResult extends UCommandResult {
    @JsonParam("targetIp")
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
}
