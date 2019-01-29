package com.ucloud.library.netanalysis.command.net;


import android.text.TextUtils;

import com.ucloud.library.netanalysis.command.UCommandTask;
import com.ucloud.library.netanalysis.utils.JLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joshua on 2018/9/4 14:06.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public abstract class UNetCommandTask<T> extends UCommandTask<T> {
    protected static final String MATCH_TRACE_IP = "(?<=From )(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
    protected static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
    protected static final String MATCH_PING_TIME = "(?<=time=).*?ms";
    protected static final String MATCH_PING_TTL = "(?<=ttl=).*(?= time)";
    protected static final String MATCH_TTL_EXCEEDED = "Time to live exceeded";
    
    protected Matcher matcherRouteNode(String str) {
        Pattern patternTrace = Pattern.compile(MATCH_TRACE_IP);
        return patternTrace.matcher(str);
    }
    
    protected Matcher matcherTime(String str) {
        Pattern patternTime = Pattern.compile(MATCH_PING_TIME);
        return patternTime.matcher(str);
    }
    
    protected Matcher matcherTTL(String str) {
        Pattern patternTime = Pattern.compile(MATCH_PING_TTL);
        return patternTime.matcher(str);
    }
    
    protected Matcher matcherIp(String str) {
        Pattern patternIp = Pattern.compile(MATCH_PING_IP);
        return patternIp.matcher(str);
    }
    
    protected Matcher matcherTTLExceeded(String str) {
        Pattern patternIp = Pattern.compile(MATCH_TTL_EXCEEDED);
        return patternIp.matcher(str);
    }
    
    protected String getIpFromMatcher(Matcher matcher) {
        String pingIp = matcher.group();
        int start = pingIp.indexOf('(');
        if (start >= 0)
            pingIp = pingIp.substring(start + 1);
        
        return pingIp;
    }
    
    protected String getPingDelayFromMatcher(Matcher matcher) {
        String time = "0";
        if (matcher.find()) {
            time = matcher.group();
            if (!TextUtils.isEmpty(time))
                time = time.replace(" ms", "");
        }
        
        return time.trim();
    }
    
    protected String getPingTTLFromMatcher(Matcher matcher) {
        String ttl = "0";
        if (matcher.find()) {
            ttl = matcher.group();
        }
        
        return ttl.trim();
    }
}
