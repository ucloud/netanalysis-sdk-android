package com.ucloud.library.netanalysis.command.net.ping;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.UNetCommandTask;
import com.ucloud.library.netanalysis.utils.JLog;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by joshua on 2018/9/4 10:21.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
final class PingTask extends UNetCommandTask<List<SinglePackagePingResult>> {
    private InetAddress targetAddress;
    private int count;
    private int currentCount;
    
    private PingCallback2 callback;
    
    PingTask(@NonNull InetAddress targetAddress) {
        this(targetAddress, 4, null);
    }
    
    PingTask(@NonNull InetAddress targetAddress, PingCallback2 callback) {
        this(targetAddress, 4, callback);
    }
    
    PingTask(@NonNull InetAddress targetAddress, int count, PingCallback2 callback) {
        this.targetAddress = targetAddress;
        this.count = count;
        this.callback = callback;
    }
    
    @Override
    public List<SinglePackagePingResult> call() throws Exception {
        resultData = running();
        return resultData;
    }
    
    private List<SinglePackagePingResult> running() {
        isRunning = true;
        
        String targetIp = targetAddress == null ? "" : targetAddress.getHostAddress();
        command = String.format("ping -c 1 -W 1 %s", targetIp);
        
        currentCount = 0;
        SinglePackagePingResult res = null;
        resultData = new ArrayList<>();
        while (isRunning && (currentCount < count)) {
            try {
                res = parseSinglePackageInput(execCommand(command));
                resultData.add(res);
                if (callback != null)
                    callback.onPing(res);
            } catch (IOException e) {
//                e.printStackTrace();
            } catch (InterruptedException e) {
//                e.printStackTrace();
            } finally {
                currentCount++;
            }
        }
        
        JLog.T(TAG, "[ping " + targetAddress.getHostAddress() + "]:" + (res == null ? "null" : res.toString()));
        return resultData;
    }
    
    private SinglePackagePingResult parseSinglePackageInput(String input) {
        JLog.T(TAG, "[icmp_seq]:" + (currentCount + 1) + " [org data]:" + input);
        SinglePackagePingResult singleRes = new SinglePackagePingResult(targetAddress.getHostAddress());
        if (TextUtils.isEmpty(input)) {
            singleRes.setStatus(UCommandStatus.CMD_STATUS_NETWORK_ERROR);
            singleRes.delaiy = 0.f;
            return singleRes;
        }
        
        Matcher matcherTargetId = matcherIp(input);
        if (matcherTargetId.find()) {
            singleRes.setStatus(UCommandStatus.CMD_STATUS_SUCCESSFUL);
            String time = getPingDelayFromMatcher(matcherTime(input));
            singleRes.setDelaiy(Float.parseFloat(time));
        } else {
            singleRes.setStatus(UCommandStatus.CMD_STATUS_FAILED);
            singleRes.delaiy = 0.f;
        }
        
        return singleRes;
    }
    
    @Override
    protected void parseInputInfo(String input) {
    
    }
    
    @Override
    protected void parseErrorInfo(String error) {
        JLog.T(TAG, "[icmp_seq]:" + (currentCount + 1) + " [error data]:" + error);
        
    }
    
    @Override
    protected void stop() {
        isRunning = false;
    }
}
