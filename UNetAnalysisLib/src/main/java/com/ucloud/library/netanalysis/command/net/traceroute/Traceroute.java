package com.ucloud.library.netanalysis.command.net.traceroute;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.ucloud.library.netanalysis.command.UCommandPerformer;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.utils.IPUtil;
import com.ucloud.library.netanalysis.utils.JLog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by joshua on 2018/9/3 15:58.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Traceroute implements UCommandPerformer {
    protected final String TAG = getClass().getSimpleName();
    private Config config;
    
    private ExecutorService threadPool;
    private TracerouteCallback callback;
    private boolean isUserStop = false;
    
    public Traceroute(@NonNull Config config, TracerouteCallback callback) {
        this.config = config == null ? new Config("") : config;
        this.callback = callback;
    }
    
    public Traceroute(String targetHost, TracerouteCallback callback) {
        this(new Config(targetHost), callback);
    }
    
    @Override
    public void run() {
        this.threadPool = Executors.newFixedThreadPool(this.config.threadSize);
        isUserStop = false;
        List<TracerouteTask> tasks = new ArrayList<>();
        InetAddress inetAddress = null;
        try {
            inetAddress = config.parseTargetAddress();
        } catch (UnknownHostException e) {
//            e.printStackTrace();
            if (callback != null)
                callback.onTracerouteFinish(null, UCommandStatus.CMD_STATUS_ERROR_UNKNOW_HOST);
            return;
        }
        
        for (int i = 1; i <= config.maxHop; i++)
            tasks.add(new TracerouteTask(inetAddress, i, config.countPerRoute,
                    (callback instanceof TracerouteCallback2 ? (TracerouteCallback2) callback : null)));
        
        List<Future<TracerouteNodeResult>> futures = null;
        long timestamp = System.currentTimeMillis() / 1000;
        try {
            long start = SystemClock.elapsedRealtime();
            futures = threadPool.invokeAll(tasks);
            JLog.D(TAG, "[invoke time]:" + (SystemClock.elapsedRealtime() - start) + " ms");
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
            stopTask();
            
            JLog.I(TAG, "[isUserStop]: " + isUserStop);
            if (futures == null) {
                if (callback != null)
                    callback.onTracerouteFinish(null, UCommandStatus.CMD_STATUS_ERROR);
                return;
            }
    
            TracerouteResult result = optResult(timestamp, futures);
            if (callback != null)
                callback.onTracerouteFinish(result, isUserStop ? UCommandStatus.CMD_STATUS_USER_STOP : UCommandStatus.CMD_STATUS_SUCCESSFUL);
        }
    }
    
    private TracerouteResult optResult(long timestamp, List<Future<TracerouteNodeResult>> futures) {
        TracerouteResult result = new TracerouteResult(config.getTargetAddress().getHostAddress(), timestamp);
        for (int i = 0, len = futures.size(); i < len; i++) {
            Future<TracerouteNodeResult> future = futures.get(i);
            if (future == null)
                continue;
            
            try {
                TracerouteNodeResult res = future.get();
                if (res == null)
                    continue;
                
                result.getTracerouteNodeResults().add(res);
                if (res.isFinalRoute())
                    break;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    private void stopTask() {
        if (threadPool != null && !threadPool.isShutdown()) {
            JLog.D(TAG, "shutdown--->" + config.targetHost);
            threadPool.shutdownNow();
        }
    }
    
    @Override
    public void stop() {
        isUserStop = true;
        stopTask();
    }
    
    public Config getConfig() {
        return config;
    }
    
    public boolean isRunning() {
        return !threadPool.isTerminated();
    }
    
    public static class Config {
        private InetAddress targetAddress;
        private String targetHost;
        private int maxHop;
        private int countPerRoute;
        private int threadSize;
        
        public Config(@NonNull String targetHost) {
            this.targetHost = targetHost;
            this.maxHop = 32;
            this.countPerRoute = 3;
            this.threadSize = 3;
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
        
        public int getThreadSize() {
            return threadSize;
        }
        
        public Config setThreadSize(int threadSize) {
            this.threadSize = Math.max(1, Math.min(threadSize, 3));
            return this;
        }
    }
}
