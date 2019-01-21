package com.ucloud.library.netanalysis.command.net.ping;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by joshua on 2018/9/3 15:58.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Ping implements UCommandPerformer {
    protected final String TAG = getClass().getSimpleName();
    private Config config;
    
    private ExecutorService threadPool;
    private PingCallback callback;
    private boolean isUserStop = false;
    
    public Ping(@NonNull Config config, PingCallback callback) {
        this.config = config == null ? new Config("") : config;
        this.callback = callback;
    }
    
    public Ping(String targetHost, PingCallback callback) {
        this(new Config(targetHost), callback);
    }
    
    @Override
    public void run() {
        this.threadPool = Executors.newSingleThreadExecutor();
        isUserStop = false;
        InetAddress inetAddress = null;
        try {
            inetAddress = config.parseTargetAddress();
        } catch (UnknownHostException e) {
//             e.printStackTrace();
            if (callback != null)
                callback.onPingFinish(null, UCommandStatus.CMD_STATUS_ERROR_UNKNOW_HOST);
            return;
        }
        
        PingTask task = new PingTask(inetAddress, config.countPerRoute,
                (callback instanceof PingCallback2 ? (PingCallback2) callback : null));
        List<PingTask> list = new ArrayList<>();
        list.add(task);
        
        List<SinglePackagePingResult> results = null;
        long timestamp = System.currentTimeMillis() / 1000;
        try {
            long start = SystemClock.elapsedRealtime();
            results = threadPool.invokeAny(list);
            JLog.D(TAG, "[invoke time]:" + (SystemClock.elapsedRealtime() - start) + " ms");
        } catch (InterruptedException e) {
//             e.printStackTrace();
        } catch (ExecutionException e) {
//             e.printStackTrace();
        } finally {
            stopTask();
            
            JLog.I(TAG, "[isUserStop]: " + isUserStop);
            if (results == null) {
                if (callback != null)
                    callback.onPingFinish(null, UCommandStatus.CMD_STATUS_ERROR);
                return;
            }
    
            PingResult result = optResult(timestamp, results);
            if (callback != null)
                callback.onPingFinish(result, isUserStop ? UCommandStatus.CMD_STATUS_USER_STOP : UCommandStatus.CMD_STATUS_SUCCESSFUL);
        }
    }
    
    private PingResult optResult(long timestamp, List<SinglePackagePingResult> res) {
        PingResult result = new PingResult(config.getTargetAddress().getHostAddress(), timestamp);
        if (res == null)
            return result;
        
        result.setPingPackages(res);
        
        return result;
    }
    
    private void stopTask() {
        if (threadPool != null && !threadPool.isShutdown()) {
            JLog.I(TAG, "shutdown--->" + config.targetHost);
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
        private int countPerRoute;
        
        public Config(@NonNull String targetHost) {
            this(targetHost, 4);
        }
        
        public Config(@NonNull String targetHost, int countPerRoute) {
            this.targetHost = targetHost;
            this.countPerRoute = countPerRoute;
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
        
        public int getCountPerRoute() {
            return countPerRoute;
        }
        
        public Config setCountPerRoute(int countPerRoute) {
            this.countPerRoute = Math.max(1, Math.min(countPerRoute, 3));
            return this;
        }
    }
}
