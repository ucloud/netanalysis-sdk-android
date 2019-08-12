package com.ucloud.library.netanalysis.command;

import android.support.annotation.NonNull;

import com.ucloud.library.netanalysis.utils.JLog;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joshua on 2019-07-22 11:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCommandRunner extends Thread {
    private final String TAG = getClass().getSimpleName();
    
    private LinkedBlockingQueue<UCommandPerformer> cmdQueue;
    private boolean isRunning;
    private UCommandPerformer currentPerformer;
    
    public UCommandRunner() {
        this("UMQA-netsdk-threadpool");
    }
    
    public UCommandRunner(@NonNull String name) {
        super(name);
        this.cmdQueue = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                currentPerformer = cmdQueue.take();
                if (isRunning)
                    currentPerformer.run();
            } catch (InterruptedException e) {
                JLog.D(TAG, "[cmd runner interrupt]:" + e == null ? "" : e.getMessage());
            }
        }
    }
    
    public boolean addCommand(UCommandPerformer performer) {
        if (cmdQueue == null)
            cmdQueue = new LinkedBlockingQueue<>();
        
        return cmdQueue.offer(performer);
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void cancel() {
        cmdQueue.clear();
    
        if (currentPerformer != null) {
            currentPerformer.stop();
            currentPerformer = null;
        }
    }
    
    public void shutdownNow() {
        if (!isRunning)
            return;
        
        cancel();
        isRunning = false;
        interrupt();
    }
}
