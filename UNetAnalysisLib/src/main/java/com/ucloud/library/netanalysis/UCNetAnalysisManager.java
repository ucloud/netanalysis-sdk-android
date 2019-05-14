package com.ucloud.library.netanalysis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.api.bean.IpInfoBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PingDataBean;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.TracerouteDataBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.callback.OnAnalyseListener;
import com.ucloud.library.netanalysis.callback.OnSdkListener;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.ping.Ping;
import com.ucloud.library.netanalysis.command.net.ping.PingCallback;
import com.ucloud.library.netanalysis.command.net.ping.PingResult;
import com.ucloud.library.netanalysis.command.net.traceroute.Traceroute;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteCallback;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteNodeResult;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteResult;
import com.ucloud.library.netanalysis.module.IpReport;
import com.ucloud.library.netanalysis.module.OptionalParam;
import com.ucloud.library.netanalysis.module.UCAnalysisResult;
import com.ucloud.library.netanalysis.module.UCNetStatus;
import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UCSdkStatus;
import com.ucloud.library.netanalysis.utils.Encryptor;
import com.ucloud.library.netanalysis.utils.JLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by joshua on 2018/8/29 18:42.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCNetAnalysisManager {
    private final String TAG = getClass().getSimpleName();
    
    public static final String SDK_VERSION = String.format("Android/%s", BuildConfig.VERSION_NAME);
    public static final int CUSTOM_IP_LIST_SIZE = 5;
    
    private final int MAX_AUTO_COMMAND_TASK_SIZE = 2;
    private final int MAX_CUSTOM_COMMAND_TASK_SIZE = 3;
    private static volatile UCNetAnalysisManager mInstance = null;
    private UCApiManager mApiManager;
    private Context mContext;
    
    private ExecutorService mAutoThreadPool;
    private ExecutorService mCustomThreadPool;
    private UNetStatusReceiver mNetStatusReceiver;
    private boolean isStartMonitorNetStatus = false;
    private TelephonyManager mTelephonyManager;
    private SignalStrength mMobileSignalStrength;
    
    private OnSdkListener mSdkListener;
    
    private IpListBean mIpListCache;
    private List<String> mReportAddr;
    private List<String> mCustomIps;
    private IpInfoBean mCurSrcIpInfo = new IpInfoBean();
    private ReentrantLock mCacheLock, mCustomLock;
    
    private String appSecret;
    private String appKey;
    private OptionalParam optionalParam;
    
    private UCNetAnalysisManager(Context context, String appKey, String appSecret) {
        this.mContext = context;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.mApiManager = new UCApiManager(mContext, appKey, appSecret);
        this.mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        this.mCacheLock = new ReentrantLock();
        this.mCustomLock = new ReentrantLock();
        this.mCustomIps = new ArrayList<>();
    }
    
    public synchronized static UCNetAnalysisManager createManager(@NonNull Context applicationContext, @NonNull String appKey, @NonNull String appSecret) {
        if (TextUtils.isEmpty(appSecret))
            throw new IllegalArgumentException("appKey is empty!");
        if (TextUtils.isEmpty(appSecret))
            throw new IllegalArgumentException("appSecret is empty!");
        appSecret = Encryptor.filterRsaKey(appSecret);
        if (TextUtils.isEmpty(appSecret))
            throw new IllegalArgumentException("appSecret is illegal!");
        
        synchronized (UCNetAnalysisManager.class) {
            destroy();
            mInstance = new UCNetAnalysisManager(applicationContext, appKey, appSecret);
        }
        
        return mInstance;
    }
    
    public static UCNetAnalysisManager getManager() {
        return mInstance;
    }
    
    public static void destroy() {
        if (mInstance != null) {
            mInstance.destroyObj();
        }
        
        mInstance = null;
    }
    
    private void destroyObj() {
        if (mAutoThreadPool != null && !mAutoThreadPool.isShutdown())
            mAutoThreadPool.shutdownNow();
        
        if (mCustomThreadPool != null && !mCustomThreadPool.isShutdown())
            mCustomThreadPool.shutdownNow();
        
        clearIpList();
        stopMonitorNetStatus();
        System.gc();
    }
    
    public void setSdkListener(OnSdkListener listener) {
        mSdkListener = listener;
    }
    
    public void register(OnSdkListener listener) {
        register(listener, null);
    }
    
    public void register(OnSdkListener listener, OptionalParam optionalParam) {
        setSdkListener(listener);
        this.optionalParam = optionalParam;
        
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)) {
            if (mSdkListener != null)
                mSdkListener.onRegister(UCSdkStatus.APPKEY_OR_APPSECRET_ILLEGAL);
            
            return;
        }
        
        startMonitorNetStatus();
        if (mSdkListener != null)
            mSdkListener.onRegister(UCSdkStatus.REGISTER_SUCCESS);
    }
    
    public void setCustomIps(List<String> ips) {
        mCustomLock.lock();
        if (mCustomThreadPool != null)
            mCustomThreadPool.shutdownNow();
        mCustomThreadPool = Executors.newFixedThreadPool(MAX_CUSTOM_COMMAND_TASK_SIZE);
        mCustomIps.clear();
        if (ips != null && !ips.isEmpty()) {
            if (ips.size() > CUSTOM_IP_LIST_SIZE) {
                for (int i = 0; i < CUSTOM_IP_LIST_SIZE; i++)
                    mCustomIps.add(ips.get(i));
            } else {
                mCustomIps.addAll(ips);
            }
        }
        mCustomLock.unlock();
        System.gc();
        
        enqueueCustom();
    }
    
    public List<String> getCustomIps() {
        List<String> list = null;
        mCustomLock.lock();
        if (mCustomIps != null) {
            list = new ArrayList<>();
            if (!mCustomIps.isEmpty())
                list.addAll(mCustomIps);
        }
        mCustomLock.unlock();
        return list;
    }
    
    private boolean isCustomAnalysing = false;
    
    private OnAnalyseListener mCustomAnalyseListener = null;
    
    public void analyse(OnAnalyseListener listener) {
        if (isCustomAnalysing)
            return;
        
        isCustomAnalysing = true;
        mCustomAnalyseListener = listener;
        // 如果手动检测触发，则强制关闭自动检测
        if (mAutoThreadPool != null)
            mAutoThreadPool.shutdownNow();
        
        if (mCustomThreadPool != null)
            mCustomThreadPool.shutdownNow();
        System.gc();
        mCustomThreadPool = Executors.newFixedThreadPool(MAX_CUSTOM_COMMAND_TASK_SIZE);
        mCustomThreadPool.execute(new CustomAnalyseRunner(mCustomAnalyseListener));
    }
    
    private class CustomAnalyseRunner implements Runnable {
        private OnAnalyseListener listener;
        private List<IpReport> reports;
        
        public CustomAnalyseRunner(OnAnalyseListener listener) {
            this.listener = listener;
            reports = new ArrayList<>();
        }
        
        @Override
        public void run() {
            mCustomLock.lock();
            if (mCustomIps == null || mCustomIps.isEmpty()) {
                JLog.E(TAG, "Your custom IP list is empty! Please make sure you have executed 'UCNetAnalysisManager.setCustomIps(List)' first.");
                UCAnalysisResult analysisResult = new UCAnalysisResult();
                analysisResult.setIpReports(reports);
                if (listener != null)
                    listener.onAnalysed(analysisResult);
                mCustomLock.unlock();
                return;
            }
            
            List<String> customIps = new ArrayList<>();
            customIps.addAll(mCustomIps);
            mCustomLock.unlock();
            
            final int size = customIps.size();
            
            PingCallback pingCallback = new PingCallback() {
                @Override
                public void onPingFinish(PingResult result, UCommandStatus status) {
                    JLog.D(TAG, result == null ? "result = null" : result.toString());
                    reportCustomIpPing(result, mReportAddr);
                    
                    IpReport report = new IpReport();
                    if (result != null) {
                        report.setIp(result.getTargetIp());
                        report.setAverageDelay(result.averageDelay());
                        report.setPackageLossRate(result.lossRate());
                    }
                    
                    report.setNetStatus(checkNetworkStatus().getNetStatus());
                    
                    reports.add(report);
                    
                    int count = reports.size();
                    if (count == size) {
                        UCAnalysisResult analysisResult = new UCAnalysisResult();
                        analysisResult.setIpReports(reports);
                        if (listener != null)
                            listener.onAnalysed(analysisResult);
                        
                        isCustomAnalysing = false;
                    }
                }
            };
            
            
            for (String ip : customIps)
                pingCustom(new Ping(new Ping.Config(ip, 5), pingCallback));
            
            if (mReportAddr != null && !mReportAddr.isEmpty())
                for (String ip : customIps)
                    tracerouteCustom(new Traceroute(new Traceroute.Config(ip).setThreadSize(5),
                            mReportCustomTracerouteCallback));
            
        }
    }
    
    public void unregister() {
        destroyObj();
    }
    
    public UCNetworkInfo checkNetworkStatus() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                networkInfo = connMgr.getActiveNetworkInfo();
            } else {
                networkInfo = checkNetworkStatus_api23_up(connMgr);
            }
        }
        JLog.T(TAG, "networkInfo--->" + (networkInfo == null ? "networkInfo = null" : networkInfo.toString()));
        UCNetworkInfo info = new UCNetworkInfo(networkInfo);
        
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    int strength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
                    int speed = wifiInfo.getLinkSpeed();
                    JLog.T(TAG, "[strength]:" + strength + " [speed]:" + speed + WifiInfo.LINK_SPEED_UNITS);
                    info.setSignalStrength(wifiInfo.getRssi());
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int strength = 0;
                if (mMobileSignalStrength != null) {
                    if (mMobileSignalStrength.isGsm()) {
                        if (mMobileSignalStrength.getGsmSignalStrength() != 99)
                            strength = mMobileSignalStrength.getGsmSignalStrength() * 2 - 113;
                        else
                            strength = mMobileSignalStrength.getGsmSignalStrength();
                    } else {
                        strength = mMobileSignalStrength.getCdmaDbm();
                    }
                }
                JLog.T(TAG, "[strength]:" + strength + " dbm");
                info.setSignalStrength(strength);
            }
        }
        
        return info;
    }
    
    /**
     * API 23及以上时调用此方法进行网络的检测
     * getAllNetworks() 在API 21后开始使用
     *
     * @param connMgr
     * @return {@link NetworkInfo}
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private NetworkInfo checkNetworkStatus_api23_up(ConnectivityManager connMgr) {
        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting())
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                    networkInfo = connMgr.getNetworkInfo(networks[i]);
                    break;
                }
            }
        
        return networkInfo;
    }
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            mMobileSignalStrength = signalStrength;
        }
    };
    
    private void startMonitorNetStatus() {
        if (isStartMonitorNetStatus)
            return;
        
        mNetStatusReceiver = new UNetStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(mNetStatusReceiver, intentFilter);
        isStartMonitorNetStatus = true;
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    
    private void stopMonitorNetStatus() {
        if (!isStartMonitorNetStatus || mNetStatusReceiver == null)
            return;
        
        mContext.unregisterReceiver(mNetStatusReceiver);
        isStartMonitorNetStatus = false;
        mNetStatusReceiver = null;
    }
    
    private void ping(String host, PingCallback callback) {
        if (TextUtils.isEmpty(host))
            throw new NullPointerException("The parameter (host) is null !");
        Ping ping = new Ping(new Ping.Config(host, 5), callback);
        ping(ping);
    }
    
    private void ping(Ping ping) {
        if (ping == null)
            throw new NullPointerException("The parameter (ping) is null !");
        
        if (mAutoThreadPool != null && !mAutoThreadPool.isShutdown())
            mAutoThreadPool.execute(ping);
    }
    
    private void pingCustom(Ping ping) {
        if (ping == null)
            throw new NullPointerException("The parameter (ping) is null !");
        
        if (mCustomThreadPool != null && !mCustomThreadPool.isShutdown())
            mCustomThreadPool.execute(ping);
    }
    
    private void traceroute(String host, TracerouteCallback callback) {
        if (TextUtils.isEmpty(host))
            throw new NullPointerException("The parameter (host) is null !");
        Traceroute traceroute = new Traceroute(new Traceroute.Config(host).setThreadSize(5),
                callback);
        traceroute(traceroute);
    }
    
    private void traceroute(Traceroute traceroute) {
        if (traceroute == null)
            throw new NullPointerException("The parameter (traceroute) is null !");
        
        if (mAutoThreadPool != null && !mAutoThreadPool.isShutdown())
            mAutoThreadPool.execute(traceroute);
    }
    
    private void tracerouteCustom(Traceroute traceroute) {
        if (traceroute == null)
            throw new NullPointerException("The parameter (traceroute) is null !");
        
        if (mCustomThreadPool != null && !mCustomThreadPool.isShutdown())
            mCustomThreadPool.execute(traceroute);
    }
    
    private Boolean flag = false;
    
    private class UNetStatusReceiver extends BroadcastReceiver {
        private final String TAG = getClass().getSimpleName();
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;
            
            if (!TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION))
                return;
            
            UCNetworkInfo info = UCNetAnalysisManager.getManager().checkNetworkStatus();
            JLog.D(TAG, "[status]:" + (info == null ? "null" : info.toString()));
            if (mSdkListener != null)
                mSdkListener.onNetworkStatusChanged(info);
            
            if (info == null || info.getNetStatus() == UCNetStatus.NET_STATUS_NOT_REACHABLE)
                return;
            synchronized (flag) {
                if (flag)
                    return;
                
                flag = true;
            }
            
            if (mAutoThreadPool != null)
                mAutoThreadPool.shutdownNow();
            mAutoThreadPool = Executors.newSingleThreadExecutor();
            
            clearIpList();
            System.gc();
            doGetPublicIpInfo();
        }
    }
    
    private void doGetPublicIpInfo() {
        mApiManager.apiGetPublicIpInfo(new Callback<PublicIpBean>() {
            @Override
            public void onResponse(Call<PublicIpBean> call, Response<PublicIpBean> response) {
                if (response == null || response.body() == null) {
                    JLog.I(TAG, "apiGetPublicIpInfo: response is null");
                    return;
                }
    
                mCurSrcIpInfo = response.body().getIpInfo();
                mCurSrcIpInfo.setNetType(checkNetworkStatus().getNetStatus().getValue());
                doGetIpList();
                synchronized (flag) {
                    flag = false;
                }
            }
            
            @Override
            public void onFailure(Call<PublicIpBean> call, Throwable t) {
                JLog.I(TAG, "apiGetPublicIpInfo failed:", t);
                synchronized (flag) {
                    flag = false;
                }
            }
        });
    }
    
    private void doGetIpList() {
        mApiManager.apiGetPingList(mCurSrcIpInfo, new Callback<UCApiResponseBean<IpListBean>>() {
            @Override
            public void onResponse(Call<UCApiResponseBean<IpListBean>> call, Response<UCApiResponseBean<IpListBean>> response) {
                if (response == null || response.body() == null) {
                    JLog.I(TAG, "apiGetPingList: response is null");
                    return;
                }
    
                UCApiResponseBean<IpListBean> body = response.body();
                if (body == null) {
                    JLog.I(TAG, "apiGetPingList: body is null");
                    return;
                }
    
                if (body.getMeta() == null) {
                    if (body.getMeta().getCode() != 200)
                        JLog.I(TAG, body.getMeta().toString());
                    
                    JLog.I(TAG, "meta is null !");
                    return;
                }
                
                if (body.getData() == null) {
                    JLog.I(TAG, "data is null !");
                    return;
                }
                
                mCacheLock.lock();
                if (randomIpList(body.getData())) {
                    mIpListCache = body.getData();
                    mReportAddr = mIpListCache.getUrl();
                }
                mCacheLock.unlock();
                
                enqueueCustom();
                enqueueAuto();
            }
            
            @Override
            public void onFailure(Call<UCApiResponseBean<IpListBean>> call, Throwable t) {
                JLog.I(TAG, "apiGetPingList failed:", t);
    
            }
        });
    }
    
    private boolean randomIpList(IpListBean bean) {
        if (bean == null)
            return false;
        
        if (bean.getInfo() == null || bean.getInfo().isEmpty())
            return false;
        
        if (bean.getUrl() == null || bean.getUrl().isEmpty())
            return false;
        
        Collections.shuffle(bean.getInfo(), new Random(SystemClock.elapsedRealtime()));
        
        return true;
    }
    
    private void enqueueAuto() {
        mCacheLock.lock();
        if (mIpListCache == null || mReportAddr == null || mReportAddr.isEmpty()) {
            mCacheLock.unlock();
            return;
        }
        
        List<IpListBean.InfoBean> list = mIpListCache.getInfo();
        mCacheLock.unlock();
        if (list == null || list.isEmpty())
            return;
        
        for (IpListBean.InfoBean info : list) {
            ping(new Ping(new Ping.Config(info.getIp(), 5), mReportPingCallback));
            traceroute(new Traceroute(new Traceroute.Config(info.getIp()).setThreadSize(5),
                    mReportTracerouteCallback));
        }
    }
    
    private void enqueueCustom() {
        mCustomLock.lock();
        if (mCustomIps == null || mCustomIps.isEmpty() || mReportAddr == null || mReportAddr.isEmpty()) {
            mCustomLock.unlock();
            return;
        }
        
        List<String> list = mCustomIps;
        mCustomLock.unlock();
        
        if (list == null || list.isEmpty())
            return;
        
        for (String ip : list) {
            ping(new Ping(new Ping.Config(ip, 5), mReportCustomPingCallback));
            traceroute(new Traceroute(new Traceroute.Config(ip).setThreadSize(5),
                    mReportCustomTracerouteCallback));
        }
    }
    
    private void enqueuePing() {
        mCacheLock.lock();
        if (mIpListCache == null || mReportAddr == null || mReportAddr.isEmpty()) {
            mCacheLock.unlock();
            return;
        }
        
        List<IpListBean.InfoBean> list = mIpListCache.getInfo();
        mCacheLock.unlock();
        if (list == null)
            return;
        for (IpListBean.InfoBean info : list)
            ping(new Ping(new Ping.Config(info.getIp(), 5), mReportPingCallback));
    }
    
    private void enqueuePingCustom() {
        mCustomLock.lock();
        if (mCustomIps == null || mCustomIps.isEmpty() || mReportAddr == null || mReportAddr.isEmpty()) {
            mCustomLock.unlock();
            return;
        }
        
        for (String ip : mCustomIps)
            ping(new Ping(new Ping.Config(ip, 5), mReportCustomPingCallback));
        
        mCustomLock.unlock();
    }
    
    private PingCallback mReportPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, result == null ? "result = null" : result.toString());
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportPing(result, mReportAddr);
        }
    };
    
    private PingCallback mReportCustomPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, result == null ? "result = null" : result.toString());
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpPing(result, mReportAddr);
        }
    };
    
    private void reportCustomIpPing(PingResult result, List<String> reportAddr) {
        reportPing(result, true, reportAddr);
    }
    
    private void reportPing(PingResult result, List<String> reportAddr) {
        reportPing(result, false, reportAddr);
    }
    
    private void reportPing(PingResult result, boolean isCustomIp, List<String> reportAddr) {
        if (result == null || reportAddr == null || reportAddr.isEmpty())
            return;
        
        List<String> reportArrdCache = new ArrayList<>();
        reportArrdCache.addAll(reportAddr);
        
        PingDataBean report = new PingDataBean();
        report.setTimestamp(result.getTimestamp());
        report.setDelay(result.averageDelay());
        report.setLoss(result.lossRate());
        report.setTTL(result.accessTTL());
        report.setDst_ip(result.getTargetIp());
        
        for (int i = 0, len = reportArrdCache.size(); i < len; i++) {
            try {
                Response<UCApiResponseBean<MessageBean>> response = mApiManager.apiReportPing(reportArrdCache.get(i), report, isCustomIp, mCurSrcIpInfo, optionalParam);
                JLog.D(TAG, "[response]:" + (response == null || response.body() == null ? "null" : response.body().toString()));
                if (response != null && response.body() != null && response.body().getMeta() != null
                        && response.body().getMeta().getCode() == 200)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void enqueueTraceroute() {
        mCacheLock.lock();
        if (mIpListCache == null || mReportAddr == null || mReportAddr.isEmpty()) {
            mCacheLock.unlock();
            return;
        }
        
        List<IpListBean.InfoBean> list = mIpListCache.getInfo();
        mCacheLock.unlock();
        if (list == null)
            return;
        for (IpListBean.InfoBean info : list)
            traceroute(new Traceroute(new Traceroute.Config(info.getIp()).setThreadSize(5),
                    mReportTracerouteCallback));
    }
    
    private void enqueueTracerouteCustom() {
        mCustomLock.lock();
        if (mCustomIps == null || mCustomIps.isEmpty()
                || mReportAddr == null || mReportAddr.isEmpty()) {
            mCustomLock.unlock();
            return;
        }
        
        for (String ip : mCustomIps)
            traceroute(new Traceroute(new Traceroute.Config(ip).setThreadSize(5),
                    mReportCustomTracerouteCallback));
        
        mCustomLock.unlock();
    }
    
    private TracerouteCallback mReportTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, result == null ? "result = null" : result.toString());
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportTraceroute(result, mReportAddr);
        }
    };
    
    private TracerouteCallback mReportCustomTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, result == null ? "result = null" : result.toString());
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpTraceroute(result, mReportAddr);
        }
    };
    
    private void reportCustomIpTraceroute(TracerouteResult result, List<String> reportAddr) {
        reportTraceroute(result, true, reportAddr);
    }
    
    private void reportTraceroute(TracerouteResult result, List<String> reportAddr) {
        reportTraceroute(result, false, reportAddr);
    }
    
    private void reportTraceroute(TracerouteResult result, boolean isCustomIp, List<String> reportAddr) {
        if (result == null || reportAddr == null || reportAddr.isEmpty())
            return;
        
        List<String> reportArrdCache = new ArrayList<>();
        reportArrdCache.addAll(reportAddr);
        
        TracerouteDataBean report = new TracerouteDataBean();
        report.setTimestamp(result.getTimestamp());
        List<TracerouteDataBean.RouteInfoBean> routeInfoBeans = new ArrayList<>();
        for (TracerouteNodeResult node : result.getTracerouteNodeResults()) {
            TracerouteDataBean.RouteInfoBean route = new TracerouteDataBean.RouteInfoBean();
            route.setRouteIp(node.getRouteIp());
            route.setDelay(node.averageDelay());
            route.setLoss(node.lossRate());
            routeInfoBeans.add(route);
        }
        report.setRouteInfoList(routeInfoBeans);
        report.setDst_ip(result.getTargetIp());
        
        for (int i = 0, len = reportArrdCache.size(); i < len; i++) {
            try {
                Response<UCApiResponseBean<MessageBean>> response = mApiManager.apiReportTraceroute(reportArrdCache.get(i), report, isCustomIp, mCurSrcIpInfo, optionalParam);
                JLog.D(TAG, "[response]:" + (response == null || response.body() == null ? "null" : response.body().toString()));
                if (response != null && response.body() != null && response.body().getMeta() != null
                        && response.body().getMeta().getCode() == 200)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void clearIpList() {
        mCacheLock.lock();
        mIpListCache = null;
        if (mReportAddr == null)
            mReportAddr = new ArrayList<>();
        else
            mReportAddr.clear();
        mCacheLock.unlock();
    }
}
