package com.ucloud.library.netanalysis.command.net.traceroute;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/9/6 16:22.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteResult implements JsonSerializable {
    @JsonParam("targetIp")
    private String targetIp;
    @JsonParam("tracerouteNodeResults")
    private List<TracerouteNodeResult> tracerouteNodeResults;
    @JsonParam("timestamp")
    private long timestamp;
    
    public TracerouteResult(String targetIp, long timestamp) {
        this.targetIp = targetIp;
        this.timestamp = timestamp;
        tracerouteNodeResults = new ArrayList<>();
    }
    
    public String getTargetIp() {
        return targetIp;
    }
    
    public List<TracerouteNodeResult> getTracerouteNodeResults() {
        return tracerouteNodeResults;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        return null;
    }
}
