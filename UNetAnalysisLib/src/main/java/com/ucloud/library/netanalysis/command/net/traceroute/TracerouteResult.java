package com.ucloud.library.netanalysis.command.net.traceroute;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/9/6 16:22.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class TracerouteResult implements JsonSerializable {
    private String targetIp;
    private List<TracerouteNodeResult> tracerouteNodeResults;
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
        JSONObject json = new JSONObject();
        JSONArray jarr = new JSONArray();
        if (tracerouteNodeResults != null && !tracerouteNodeResults.isEmpty()) {
            for (TracerouteNodeResult result : tracerouteNodeResults) {
                if (result == null || result.toJson().length() == 0)
                    continue;
                
                jarr.put(result.toJson());
            }
        }
        try {
            json.put("targetIp", targetIp);
            json.put("timestamp", timestamp);
            json.put("tracerouteNodeResults", jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
