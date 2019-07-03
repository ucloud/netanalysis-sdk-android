package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.ping.PingResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2019/5/30 14:30.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PingDomainResult implements JsonSerializable {
    @JsonParam("PingResult")
    private PingResult pingResult;
    @JsonParam("CommandStatus")
    private UCommandStatus status;

    public PingDomainResult(PingResult pingResult, UCommandStatus status) {
        this.pingResult = pingResult;
        this.status = status;
    }

    public PingResult getPingResult() {
        return pingResult;
    }

    public void setPingResult(PingResult pingResult) {
        this.pingResult = pingResult;
    }

    public UCommandStatus getStatus() {
        return status;
    }

    public void setStatus(UCommandStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("PingResult", pingResult == null ? JSONObject.NULL : pingResult.toJson());
            json.put("CommandStatus", status.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
