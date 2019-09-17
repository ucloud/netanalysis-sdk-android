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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.api.bean.IpInfoBean;
import com.ucloud.library.netanalysis.api.bean.IpListBean;
import com.ucloud.library.netanalysis.api.bean.MessageBean;
import com.ucloud.library.netanalysis.api.bean.PingDataBean;
import com.ucloud.library.netanalysis.api.bean.PingDomainResult;
import com.ucloud.library.netanalysis.api.bean.PublicIpBean;
import com.ucloud.library.netanalysis.api.bean.SdkStatus;
import com.ucloud.library.netanalysis.api.bean.TracerouteDataBean;
import com.ucloud.library.netanalysis.api.bean.UCApiResponseBean;
import com.ucloud.library.netanalysis.api.http.Response;
import com.ucloud.library.netanalysis.callback.OnSdkListener;
import com.ucloud.library.netanalysis.command.UCommandPerformer;
import com.ucloud.library.netanalysis.command.UCommandRunner;
import com.ucloud.library.netanalysis.command.bean.UCommandStatus;
import com.ucloud.library.netanalysis.command.net.ping.Ping;
import com.ucloud.library.netanalysis.command.net.ping.PingCallback;
import com.ucloud.library.netanalysis.command.net.ping.PingResult;
import com.ucloud.library.netanalysis.command.net.traceroute.Traceroute;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteCallback;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteNodeResult;
import com.ucloud.library.netanalysis.command.net.traceroute.TracerouteResult;
import com.ucloud.library.netanalysis.exception.UCHttpException;
import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.module.UCNetStatus;
import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UCSdkStatus;
import com.ucloud.library.netanalysis.utils.UCConfig;
import com.ucloud.library.netanalysis.utils.Encryptor;
import com.ucloud.library.netanalysis.utils.JLog;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by joshua on 2018/8/29 18:42.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
class UCNetAnalysisManager {
    private final String TAG = getClass().getSimpleName();
    
    private UCApiManager mApiManager;
    private Context mContext;
    
    private UNetStatusReceiver mNetStatusReceiver;
    private Boolean isRegisted = false;
    private TelephonyManager mTelephonyManager;
    private SignalStrength mMobileSignalStrength;
    
    private OnSdkListener mSdkListener;
    
    private List<IpListBean.InfoBean> mUcloudIps;
    private List<String> mReportAddr;
    private List<String> mCustomIps;
    private IpInfoBean mCurSrcIpInfo;
    
    private UserDefinedData userDefinedData;
    private UCConfig config;
    
    private String mDomain;
    private PingDomainResult mDomainResult;
    private UCommandRunner mCommandRunner;
    
    UCNetAnalysisManager(@NonNull Context applicationContext,
                         @NonNull String appKey, @NonNull String appSecret) {
        if (TextUtils.isEmpty(appKey))
            throw new IllegalArgumentException("appKey is empty!");
        if (TextUtils.isEmpty(appSecret))
            throw new IllegalArgumentException("appSecret is empty!");
        appSecret = Encryptor.filterRsaKey(appSecret);
        if (TextUtils.isEmpty(appSecret))
            throw new IllegalArgumentException("appSecret is illegal!");
        
        try {
            this.mContext = applicationContext;
            this.mApiManager = new UCApiManager(applicationContext, appKey, Encryptor.getPublicKey(appSecret));
            this.mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            this.mCustomIps = new ArrayList<>();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("appSecret is invalid!", e);
        }
    }
    
    synchronized void destroy() {
        destroyObj();
        mCommandRunner = null;
    }
    
    private synchronized void destroyObj() {
        if (mCommandRunner != null)
            mCommandRunner.shutdownNow();
        
        clearCache();
        System.gc();
    }
    
    synchronized void setSdkListener(OnSdkListener listener) {
        mSdkListener = listener;
    }
    
    synchronized void register(@NonNull OnSdkListener listener) {
        register(listener, new UCConfig());
    }
    
    synchronized void register(@NonNull OnSdkListener listener, @NonNull UCConfig config) {
        setSdkListener(listener);
        
        synchronized (isRegisted) {
            // 判断是否已注册过
            if (isRegisted) {
                if (this.mSdkListener != null)
                    this.mSdkListener.onRegister(UCSdkStatus.SDK_HAS_BEEN_REGISTERED);
                return;
            }
        }
        
        synchronized (isRegisting) {
            if (isRegisting) {
                if (this.mSdkListener != null)
                    this.mSdkListener.onRegister(UCSdkStatus.SDK_IS_REGISTING);
                return;
            }
            
            isRegisting = true;
        }
        
        // 执行配置选项
        this.config = config == null ? new UCConfig() : config;
        this.config.handleConfig();
        
        if (mCommandRunner != null)
            mCommandRunner.shutdownNow();
        mCommandRunner = new UCommandRunner();
        mCommandRunner.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                {
                    try {
                        Response<UCApiResponseBean<SdkStatus>> response = mApiManager.apiGetSdkStatus();
                        if (response == null || response.body() == null) {
                            synchronized (isRegisting) {
                                isRegisting = false;
                            }
                            if (mSdkListener != null)
                                mSdkListener.onRegister(UCSdkStatus.OBTAIN_AUTH_FAILED);
                            return;
                        }
                        UCApiResponseBean<SdkStatus> body = response.body();
                        if (body.getData() == null) {
                            synchronized (isRegisting) {
                                isRegisting = false;
                            }
                            if (mSdkListener != null)
                                mSdkListener.onRegister(UCSdkStatus.OBTAIN_AUTH_FAILED);
                            return;
                        }
                        
                        if (body.getData().getEnabled() == 1) {
                            startMonitorNetStatus();
                            
                            synchronized (isRegisting) {
                                isRegisting = false;
                            }
                            
                            synchronized (isRegisted) {
                                isRegisted = true;
                            }
                            
                            if (mSdkListener != null)
                                mSdkListener.onRegister(UCSdkStatus.REGISTER_SUCCESS);
                        } else {
                            synchronized (isRegisting) {
                                isRegisting = false;
                            }
                            if (mSdkListener != null)
                                mSdkListener.onRegister(UCSdkStatus.SDK_IS_CLOSED_BY_REMOTE);
                            return;
                        }
                    } catch (UCHttpException e) {
                        JLog.W(TAG, "obtain auth failed: " + e.getMessage());
                        synchronized (isRegisting) {
                            isRegisting = false;
                        }
                        if (mSdkListener != null)
                            mSdkListener.onRegister(UCSdkStatus.OBTAIN_AUTH_FAILED);
                    }
                }
            }
        }).start();
    }
    
    private Boolean isRegisting = false;
    
    synchronized void unregister() {
        synchronized (isRegisted) {
            stopMonitorNetStatus();
            destroyObj();
            isRegisted = false;
        }
    }
    
    synchronized void setCustomIps(List<String> ips) {
        List<String> cache = ips == null ? new ArrayList<String>() : new ArrayList<>(ips.subList(0, Math.min(UmqaClient.CUSTOM_IP_LIST_SIZE, ips.size())));
        
        Collections.sort(cache);
        boolean isSame = mCustomIps.size() == cache.size();
        if (isSame) {
            for (int i = 0, len = cache.size(); isSame && i < len; i++) {
                if (!(isSame = TextUtils.equals(mCustomIps.get(i), cache.get(i))))
                    break;
            }
        }
        
        System.gc();
        
        if (isSame) {
            JLog.I(TAG, "These are same to current custom IPs");
            return;
        }
        
        mCustomIps = cache;
        
        if (mCustomIps.isEmpty())
            return;
        
        synchronized (isRegisted) {
            if (!isRegisted)
                return;
        }
        
        if (config.isAutoDetect()) {
            if (mDomainResult == null)
                checkDomain();
            detectCustomIP(false);
        }
    }
    
    synchronized void setUserDefinedData(UserDefinedData userDefinedData) {
        this.userDefinedData = userDefinedData == null ? null : userDefinedData.copy();
    }
    
    synchronized List<String> getCustomIps() {
        return new ArrayList<>(mCustomIps);
    }
    
    
    synchronized boolean analyse() {
        // 如果没有register则无法进行检测
        synchronized (isRegisted) {
            if (!isRegisted)
                return false;
        }
        
        if (mCommandRunner != null)
            mCommandRunner.cancel();
        
        if (mCommandRunner != null && mCommandRunner.isAlive())
            mCommandRunner.addCommand(new UCommandPerformer() {
                private boolean isRunning;
                private int size;
                
                @Override
                public void run() {
                    isRunning = true;
                    List<String> cache = new ArrayList<>(mCustomIps);
                    size = cache.size();
                    if (size == 0) {
                        JLog.W(TAG, "Your custom IP list is empty!");
                        
                        if (isRunning && !config.isAutoDetect()) {
                            doGetIpList();
                            if (isRunning)
                                checkDomain();
                            if (isRunning)
                                detectUCloudIP(true);
                        }
                    } else {
                        doGetIpList();
                        if (isRunning)
                            checkDomain();
                        if (isRunning)
                            detectCustomIP(true);
                        if (isRunning && !config.isAutoDetect())
                            detectUCloudIP(true);
                    }
                }
                
                @Override
                public void stop() {
                    isRunning = false;
                    if (size > 0 || (size == 0 && !config.isAutoDetect()))
                        isCheckingDomain(-1);
                }
            });
        
        return true;
    }
    
    UCNetworkInfo checkNetworkStatus() {
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
                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        int strength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
                        int speed = wifiInfo.getLinkSpeed();
                        JLog.T(TAG, "[strength]:" + strength + " [speed]:" + speed + WifiInfo.LINK_SPEED_UNITS);
                        info.setSignalStrength(wifiInfo.getRssi());
                    }
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
        //通过循环将网络信息逐个取出来
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo == null || !networkInfo.isConnected()) {
            for (int i = 0, len = networks.length; i < len; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo != null && networkInfo.isConnected())
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
        if (mNetStatusReceiver != null)
            return;
        
        mNetStatusReceiver = new UNetStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(mNetStatusReceiver, intentFilter);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    
    private void stopMonitorNetStatus() {
        if (mNetStatusReceiver == null)
            return;
        
        mContext.unregisterReceiver(mNetStatusReceiver);
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
        
        if (mCommandRunner != null && mCommandRunner.isAlive()) {
            boolean res = mCommandRunner.addCommand(ping);
            JLog.T(TAG, "[add task ping]: " + ping.getConfig().getTargetHost() + " res:" + res);
        }
    }
    
    private void traceroute(String host, TracerouteCallback callback) {
        if (TextUtils.isEmpty(host))
            throw new NullPointerException("The parameter (host) is null !");
        
        Traceroute traceroute = new Traceroute(new Traceroute.Config(host),
                callback);
        traceroute(traceroute);
    }
    
    private void traceroute(Traceroute traceroute) {
        if (traceroute == null)
            throw new NullPointerException("The parameter (traceroute) is null !");
        
        if (mCommandRunner != null && mCommandRunner.isAlive()) {
            boolean res = mCommandRunner.addCommand(traceroute);
            JLog.T(TAG, "[add task traceroute]: " + traceroute.getConfig().getTargetHost() + " res:" + res);
        }
    }
    
    private Boolean isGettingIpInfo = false;
    
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
            
            UCNetworkInfo info = UCNetAnalysisManager.this.checkNetworkStatus();
            JLog.D(TAG, "[status]:" + (info == null ? "null" : info.toString()));
            if (mSdkListener != null)
                mSdkListener.onNetworkStatusChanged(info);
            
            if (info == null || info.getNetStatus() == UCNetStatus.NET_STATUS_NOT_REACHABLE)
                return;
            
            synchronized (isGettingIpInfo) {
                if (isGettingIpInfo)
                    return;
                
                isGettingIpInfo = true;
            }
            
            if (mCommandRunner != null)
                mCommandRunner.cancel();
            
            mCurSrcIpInfo = null;
            mDomainResult = null;
            mDomain = null;
            System.gc();
            if (mCommandRunner != null && mCommandRunner.isAlive()) {
                mCommandRunner.addCommand(new UCommandPerformer() {
                    private boolean isRunning = true;
                    
                    @Override
                    public void stop() {
                        isRunning = false;
                        synchronized (isGettingIpInfo) {
                            isGettingIpInfo = false;
                        }
                    }
                    
                    @Override
                    public void run() {
                        if (isRunning)
                            doGetPublicIpInfo();
                        
                        synchronized (isGettingIpInfo) {
                            isGettingIpInfo = false;
                        }
                        
                        if (!isRunning || !config.isAutoDetect())
                            return;
                        
                        doGetIpList();
                        if (isRunning)
                            startAutoDetection();
                    }
                });
            }
        }
    }
    
    private void doGetPublicIpInfo() {
        try {
            Response<PublicIpBean> response = mApiManager.apiGetPublicIpInfo();
            if (response == null || response.body() == null) {
                JLog.I(TAG, "apiGetPublicIpInfo: response is null");
                return;
            }
            
            mCurSrcIpInfo = response.body().getIpInfo();
            mCurSrcIpInfo.setNetType(checkNetworkStatus().getNetStatus().getValue());
        } catch (UCHttpException e) {
            JLog.I(TAG, "apiGetPublicIpInfo error:\n" + e.getMessage());
        }
    }
    
    private void doGetIpList() {
        clearIpList();
        try {
            Response<UCApiResponseBean<IpListBean>> response = mApiManager.apiGetPingList(mCurSrcIpInfo);
            if (response == null) {
                JLog.I(TAG, "apiGetPingList: response is null");
                return;
            }
            
            UCApiResponseBean<IpListBean> body = response.body();
            if (body == null) {
                JLog.I(TAG, "apiGetPingList: body is null");
                return;
            }
            
            if (body.getMeta() == null) {
                JLog.I(TAG, "meta is null !");
                return;
            }
            
            if (body.getMeta().getCode() != 200)
                JLog.I(TAG, body.getMeta().toString());
            
            if (body.getData() == null) {
                JLog.I(TAG, "data is null !");
                return;
            }
            
            if (randomIpList(body.getData())) {
                IpListBean bean = body.getData();
                mUcloudIps = bean.getInfo();
                mReportAddr = bean.getUrl();
                mDomain = bean.getDomain();
            }
        } catch (UCHttpException e) {
            JLog.I(TAG, "apiGetPingList error:\n" + e.getMessage());
        }
    }
    
    private void startAutoDetection() {
        checkDomain();
        detectUCloudIP(false);
        detectCustomIP(false);
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
    
    private Boolean isCheckingDomain = false;
    
    private boolean isCheckingDomain(int flag) {
        synchronized (isCheckingDomain) {
            isCheckingDomain = flag > 0 || (flag < 0 ? false : isCheckingDomain);
        }
        return isCheckingDomain;
    }
    
    private synchronized void checkDomain() {
        if (TextUtils.isEmpty(mDomain) || mReportAddr == null || mReportAddr.isEmpty())
            return;
        
        if (isCheckingDomain(0))
            return;
        
        isCheckingDomain(1);
        
        mDomainResult = null;
        ping(mDomain, mDomainPingCallback);
    }
    
    private void detectUCloudIP(boolean isManual) {
        if (mUcloudIps == null || mUcloudIps.isEmpty() || mReportAddr == null || mReportAddr.isEmpty())
            return;
        
        List<IpListBean.InfoBean> list = new ArrayList<>(mUcloudIps);
        if (list == null || list.isEmpty())
            return;
        
        JLog.I(TAG, String.format("start auto UCloud IP list(%d) detection ...", list.size()));
        for (IpListBean.InfoBean info : list) {
            ping(new Ping(new Ping.Config(info.getIp(), 5),
                    isManual ? mReportManualUCloudPingCallback : mReportAutoUCloudPingCallback));
            if (info.isNeedTraceroute())
                traceroute(new Traceroute(new Traceroute.Config(info.getIp()),
                        isManual ? mReportManualUCloudTracerouteCallback : mReportAutoUCloudTracerouteCallback));
        }
    }
    
    private void detectCustomIP(boolean isManual) {
        if (mCustomIps == null || mCustomIps.isEmpty() || mReportAddr == null || mReportAddr.isEmpty())
            return;
        
        List<String> list = new ArrayList<>(mCustomIps);
        
        if (list.isEmpty())
            return;
        
        JLog.I(TAG, String.format("start auto custom IP list(%d) detection ...", list.size()));
        for (String ip : list) {
            ping(new Ping(new Ping.Config(ip, 5),
                    isManual ? mReportManualCustomPingCallback : mReportAutoCustomPingCallback));
            traceroute(new Traceroute(new Traceroute.Config(ip),
                    isManual ? mReportManualCustomTracerouteCallback : mReportAutoCustomTracerouteCallback));
        }
    }
    
    private PingCallback mDomainPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[ping domain]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            isCheckingDomain(-1);
            mDomainResult = new PingDomainResult(result, status);
        }
    };
    
    /**
     * ***************************** PingCallback *************************************
     */
    private PingCallback mReportAutoUCloudPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportUCloudPing(result, mReportAddr, false);
        }
    };
    
    private PingCallback mReportManualUCloudPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportUCloudPing(result, mReportAddr, true);
        }
    };
    
    private PingCallback mReportAutoCustomPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpPing(result, mReportAddr, false);
        }
    };
    
    private PingCallback mReportManualCustomPingCallback = new PingCallback() {
        @Override
        public void onPingFinish(PingResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpPing(result, mReportAddr, true);
        }
    };
    
    /**
     * ***************************** TracerouteCallback *************************************
     */
    private TracerouteCallback mReportAutoUCloudTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportUCloudTraceroute(result, mReportAddr, false);
        }
    };
    
    private TracerouteCallback mReportManualUCloudTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportUCloudTraceroute(result, mReportAddr, true);
        }
    };
    
    private TracerouteCallback mReportAutoCustomTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpTraceroute(result, mReportAddr, false);
        }
    };
    
    private TracerouteCallback mReportManualCustomTracerouteCallback = new TracerouteCallback() {
        @Override
        public void onTracerouteFinish(TracerouteResult result, UCommandStatus status) {
            JLog.D(TAG, String.format("[status]:%s [res]:%s", status.name(), result == null ? "null" : result.toString()));
            if (result == null || status != UCommandStatus.CMD_STATUS_SUCCESSFUL)
                return;
            
            reportCustomIpTraceroute(result, mReportAddr, true);
        }
    };
    
    
    /**
     * ********************************  Report Ping Method  **************************************
     */
    private void reportCustomIpPing(PingResult result, List<String> reportAddr, boolean isManual) {
        reportPing(result, true, reportAddr, isManual);
    }
    
    private void reportUCloudPing(PingResult result, List<String> reportAddr, boolean isManual) {
        reportPing(result, false, reportAddr, isManual);
    }
    
    private void reportPing(PingResult result, boolean isCustomIp, List<String> reportAddr, boolean isManual) {
        if (result == null || reportAddr == null || reportAddr.isEmpty())
            return;
        
        List<String> reportArrdCache = new ArrayList<>(reportAddr);
        
        PingDataBean report = new PingDataBean();
        report.setTimestamp(result.getTimestamp());
        report.setDelay(result.averageDelay());
        report.setLoss(result.lossRate());
        report.setTTL(result.accessTTL());
        report.setDst_ip(result.getTargetIp());
        int pingStatus = 2;
        
        if (result.lossRate() < 100) {
            pingStatus = 0;
        } else {
            if (mDomainResult != null
                    && mDomainResult.getPingResult() != null
                    && mDomainResult.getStatus().equals(UCommandStatus.CMD_STATUS_SUCCESSFUL)) {
                pingStatus = mDomainResult.getPingResult().lossRate() < 100 ? 0 : 1;
            }
        }
        
        for (int i = 0, len = reportArrdCache.size(); i < len; i++) {
            if (mCurSrcIpInfo == null)
                return;
            try {
                Response<UCApiResponseBean<MessageBean>> response = mApiManager.apiReportPing(reportArrdCache.get(i), report,
                        pingStatus, isCustomIp, mCurSrcIpInfo, isManual, userDefinedData);
                JLog.D(TAG, "[response]:" + (response == null || response.body() == null ? "null" : response.body().toString()));
                if (response != null && response.body() != null && response.body().getMeta() != null
                        && response.body().getMeta().getCode() == 200)
                    break;
            } catch (UCHttpException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * ********************************  Report Ping Method  **************************************
     */
    private void reportCustomIpTraceroute(TracerouteResult result, List<String> reportAddr, boolean isManual) {
        reportTraceroute(result, true, reportAddr, isManual);
    }
    
    private void reportUCloudTraceroute(TracerouteResult result, List<String> reportAddr, boolean isManual) {
        reportTraceroute(result, false, reportAddr, isManual);
    }
    
    private void reportTraceroute(TracerouteResult result, boolean isCustomIp, List<
            String> reportAddr, boolean isManual) {
        if (result == null || reportAddr == null || reportAddr.isEmpty())
            return;
        
        List<String> reportArrdCache = new ArrayList<>(reportAddr);
        
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
            if (mCurSrcIpInfo == null)
                return;
            try {
                Response<UCApiResponseBean<MessageBean>> response = mApiManager.apiReportTraceroute(reportArrdCache.get(i), report,
                        isCustomIp, mCurSrcIpInfo, isManual, userDefinedData);
                JLog.D(TAG, "[response]:" + (response == null || response.body() == null ? "null" : response.body().toString()));
                if (response != null && response.body() != null && response.body().getMeta() != null
                        && response.body().getMeta().getCode() == 200)
                    break;
            } catch (UCHttpException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void clearIpList() {
        if (mUcloudIps != null)
            mUcloudIps.clear();
        if (mReportAddr != null)
            mReportAddr.clear();
    }
    
    private void clearCache() {
        clearIpList();
        if (mCustomIps != null)
            mCustomIps.clear();
    }
}
