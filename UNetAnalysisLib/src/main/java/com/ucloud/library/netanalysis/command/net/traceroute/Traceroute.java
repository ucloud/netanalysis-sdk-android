package com.ucloud.library.netanalysis.command.net.traceroute;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.command.UCommandPerformer;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.utils.IPUtil;
import com.ucloud.library.netanalysis.utils.JLog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/9/3 15:58.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Traceroute implements UCommandPerformer {
    protected final String TAG = getClass().getSimpleName();
    private Config config;
    
    private TracerouteCallback callback;
    private boolean isUserStop = false;
    private TracerouteTask task;
    
    public Traceroute(Config config, TracerouteCallback callback) {
        this.config = config == null ? new Config("") : config;
        this.callback = callback;
    }
    
    public Traceroute(String targetHost, TracerouteCallback callback) {
        this(new Config(targetHost), callback);
    }
    
    @Override
    public void run() {
        JLog.T(TAG, "run thread:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());
        isUserStop = false;
        InetAddress inetAddress;
        try {
            inetAddress = config.parseTargetAddress();
        } catch (UnknownHostException e) {
            JLog.W(TAG, String.format("traceroute parse %s occur error:%s ", config.targetHost, e.getMessage()));
            if (callback != null)
                callback.onTracerouteFinish(null, UCommandStatus.CMD_STATUS_ERROR_UNKNOW_HOST);
            return;
        }
        
        List<TracerouteNodeResult> nodeResults = new ArrayList<>();
        int countUnreachable = 0;
        long timestamp = System.currentTimeMillis() / 1000;
        long start = SystemClock.elapsedRealtime();
        for (int i = 1; i <= config.maxHop && !isUserStop; i++) {
            task = new TracerouteTask(inetAddress, i, config.countPerRoute,
                    (callback instanceof TracerouteCallback2 ? (TracerouteCallback2) callback : null));
            TracerouteNodeResult node = task.run();
            JLog.D(TAG, String.format("[thread]:%d, [trace node]:%s", Thread.currentThread().getId(),
                    (node == null ? "null" : node.toString())));
            if (node == null)
                continue;
            
            nodeResults.add(node);
            if (node.isFinalRoute())
                break;
            
            if (TextUtils.equals("*", node.getRouteIp()))
                countUnreachable++;
            else
                countUnreachable = 0;
            
            if (countUnreachable == 5)
                break;
        }
        JLog.D(TAG, "[invoke time]:" + (SystemClock.elapsedRealtime() - start) + " ms");
        
        TracerouteResult result = new TracerouteResult(config.getTargetAddress().getHostAddress(), timestamp);
        result.getTracerouteNodeResults().addAll(nodeResults);
        
        if (callback != null)
            callback.onTracerouteFinish(result, isUserStop ? UCommandStatus.CMD_STATUS_USER_STOP : UCommandStatus.CMD_STATUS_SUCCESSFUL);
    }
    
    
    @Override
    public void stop() {
        isUserStop = true;
        if (task != null)
            task.stop();
    }
    
    public Config getConfig() {
        return config;
    }
    
    public static class Config {
        private InetAddress targetAddress;
        private String targetHost;
        private int maxHop;
        private int countPerRoute;
        
        public Config(@NonNull String targetHost) {
            this.targetHost = targetHost;
            this.maxHop = 32;
            this.countPerRoute = 3;
        }
        
        InetAddress getTargetAddress() {
            return targetAddress;
        }
        
        InetAddress parseTargetAddress() throws UnknownHostException {
            targetAddress = IPUtil.parseIPv4Address(targetHost);
            return targetAddress;
        }
        
        public String getTargetHost() {
            return targetHost;
        }
        
        public Config setTargetHost(@NonNull String targetHost) {
            this.targetHost = targetHost;
            return this;
        }
        
        public int getMaxHop() {
            return maxHop;
        }
        
        public Config setMaxHop(int maxHop) {
            this.maxHop = Math.max(1, Math.min(maxHop, 128));
            return this;
        }
        
        public int getCountPerRoute() {
            return countPerRoute;
        }
        
        public Config setCountPerRoute(int countPerRoute) {
            this.countPerRoute = Math.max(1, Math.min(countPerRoute, 3));
            return this;
        }
    }
}
