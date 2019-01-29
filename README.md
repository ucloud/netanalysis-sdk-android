# UCloud NetAnalysis SDK for Android

[![](https://img.shields.io/github/release/ucloud/netanalysis-sdk-android.svg)](https://github.com/ucloud/netanalysis-sdk-android)
[![](https://img.shields.io/github/last-commit/ucloud/netanalysis-sdk-android.svg)](https://github.com/ucloud/netanalysis-sdk-android)
[![](https://img.shields.io/github/commits-since/ucloud/netanalysis-sdk-android/latest.svg)](https://github.com/ucloud/netanalysis-sdk-android)

## 运行环境
### Android
- Android系统版本：**2.3.3** (API 10)及以上


</br></br>
## 使用
### Dependencies
NetAnalysis SDK依赖于Gson、Retrofit2.0
- 将NetAnalysisLib.jar放入项目app模块中的libs目录下，并在app模块的build.gradle的dependencies中建立依赖
- 在app模块的build.gradle的dependencies中添加

    ``` java
    dependencies {
        /** 
         * your other dependencies
         */ 
        implementation 'com.google.code.gson:gson:2.8.5'
        implementation 'com.squareup.retrofit2:retrofit:2.4.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
    }
    ```

</br>

### 系统权限设置
以下是NetAnalysis SDK所需要的Android权限，请确保您的AndroidManifest.xml文件中已经配置了这些权限，否则，SDK将无法正常工作。
``` xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

</br></br>

### 快速接入
#### 1、在**自定义Application类**或者**主Activity类**的onCreate中构建UCNetAnalysisManager，并注册
``` java
// 使用Application Context 构建UCNetAnalysisManager实例
String appKey = "UCloud为您的APP分配的APP_KEY";
String appSecret = "UCloud为您的APP分配的APP_SECRET";
UCNetAnalysisManager manager = UCNetAnalysisManager.createManager(context.getApplicationContext(), appKey, appSecret);

// SDK回调
OnSdkListener sdkListener = new OnSdkListener() {
    @Override
    public void onRegister(UCSdkStatus status) {
        // SDK register完成后回调，register结果见**UCSdkStatus**说明
    }
    
    @Override
    public void onNetworkStatusChanged(UCNetworkInfo networkInfo) {
        // 网络状态改变回调，回调结果见**UCNetworkInfo**说明
    }
};

// 注册sdk模块
manager.register(sdkListener);
```

#### 2、配置你需要分析网络质量的IP地址或者域名
``` java
List<String> ips = new ArrayList();
ips.add("127.0.0.1");
ips.add("127.0.0.2");
ips.add("www.ucloud.cn");
// 配置你想要监测的网络质量的ip/域名
manager.setCustomIps(ips);
```

#### 3、开始分析你所配置的IP地址或者域名的网络质量
``` java
// 开始网络质量分析，将会分析你刚才配置的ip/域名
manager.analyse(new OnAnalyseListener() {
        @Override
        public void onAnalysed(UCAnalysisResult result) {
            // result：分析结果，详情见 UCAnalysisResult 说明
        }
    });
```

#### 4、在你的应用退出时，注销并销毁UCNetAnalysisManager
``` java
@Override
protected void onDestroy(){
    // 注销UCNetAnalysisManager模块，注销后将停止一切未完成的操作
    manager.unregister();
    // 销毁UCNetAnalysisManager实例以释放内存空间
    UCNetAnalysisManager.destroy();
    super.onDestroy();
}
```

</br></br>

## 类

### UCNetAnalysisManager
> UNetAnalysisSDK的主要模块，你将多次使用UCNetAnalysisManager的单例对象进行SDK业务接口的操作

#### 创建UCNetAnalysisManager单例对象
``` java
public static UCNetAnalysisManager createManager(Context applicationContext, String appKey, String appSecret)
```
- **param**:
    -  applicationContext: application的context
    -  appKey: UCloud为您的APP分配的APP_KEY
    -  appSecret: UCloud为您的APP分配的APP_SECRET
- **return**: UCNetAnalysisManager单例对象

#### 获取UCNetAnalysisManager单例对象
``` java
public static UCNetAnalysisManager getManager()
```

- **param**: -
- **return**: UCNetAnalysisManager单例对象，若未调用createManager，则return为null

#### 注册UCNetAnalysisManager模块
``` java
public void register(OnSdkListener listener)
```

- **param**: 
    - listener: OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: -

#### 设置Sdk回调接口
``` java
public void setSdkListener(OnSdkListener listener)
```

- **param**: 
    - listener:  OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: -

#### 设置自定义的IP地址或域名
``` java
public void setCustomIps(List<String> ips)
```

- **param**: 
    - ips:  自定义需要网络分析的IP地址或域名列表
- **return**: -

#### 获取已设置的自定义的IP地址或域名
``` java
public List<String> getCustomIps()
```

- **param**: -
- **return**: 
    - List<String>:  已设置的需要网络分析的IP地址或域名列表

#### 执行网络质量诊断
``` java
public void analyse(OnAnalyseListener listener)
```

- **param**: 
    - listener:  网络质量诊断结果回调接口，详情见**OnAnalyseListener**说明
- **return**: -

#### 检查当前设备网络状态
``` java
public UCNetworkInfo checkNetworkStatus()
```

- **param**: -
- **return**: 
    - UCNetworkInfo:  当前设备的网络状态，详情见**UCNetworkInfo**

#### 注销UCNetAnalysisManager模块
``` java
public void unregister()
```

- **param**: -
- **return**: -

#### 销毁UCNetAnalysisManager单例对象
``` java
public static void destroy()
```

- **param**: -
- **return**: -

</br></br>
### OnSdkListener
> UCNetAnalysisManager模块回调接口

``` java
public interface OnSdkListener {
    // 注册结果回调，status详情见UCSdkStatus
    void onRegister(UCSdkStatus status);
 
    // 网络情况改变回调，status详情见UCNetworkInfo
    void onNetworkStatusChanged(UCNetworkInfo status);
}
```

</br></br>
### OnAnalyseListener
> UCNetAnalysisManager模块，网络质量分析接口的回调

``` java
public interface OnAnalyseListener {
    // 网络质量分析结果回调，result详情见UCAnalysisResult
    void onAnalysed(UCAnalysisResult result);
}
```

</br></br>
### UCAnalysisResult
> 网络质量分析结果

``` java
public class UCAnalysisResult {
    // 自定义IP地址或域名列表的分析结果
    private List<IpReport> ipReports;
}
```

</br></br>
### IpReport
> 自定义IP地址或域名的分析结果

``` java
public class IpReport {
    // 目标IP
    private String ip;
 
    // 平均延时（毫秒）
    private int averageDelay;
 
    // 丢包率（0~100）
    private int packageLossRate;
 
    // 诊断该IP时的网络情况
    private UCNetStatus netStatus;
}
```

</br></br>
### UCNetworkInfo
> UNetAnalysisSDK状态集

``` java
public class UCNetworkInfo {
    // android系统网络信息类：android.net.NetworkInfo
    private NetworkInfo sysNetInfo;
 
    // UNetAnalysisSDK状态集,详情见UCNetStatus说明
    private UCNetStatus netStatus;
 
    // 信号强度（dbm）
    private int signalStrength;   
}
```


</br></br>
### UCSdkStatus
> UNetAnalysisSDK状态集

``` java
public enum UCSdkStatus {
    // 注册模块成功
    REGISTER_SUCCESS,
    // APPID或者APPKEY无效
    APPID_OR_APPKEY_ILLEGAL,
}
```


</br></br>
### UCNetStatus
> 网络状态集

``` java
public enum UCNetStatus {
    // 无网络连接
    NET_STATUS_NOT_CONNECTED,
 
    // WIFI网络
    NET_STATUS_WIFI,
 
    // 4G网络
    NET_STATUS_4G,
 
    // 3.5G网络
    NET_STATUS_3_5G,
 
    // 3G网络
    NET_STATUS_3G,
 
    // 2.5G网络
    NET_STATUS_2_5G,
 
    // 2G网络
    NET_STATUS_2G,
 
    // 未知类型
    NET_STATUS_UNKNOW,
}
```

</br></br>
### JLog
> Log打印工具

``` java
public class JLog {
    public static boolean SHOW_DEBUG = false;
    public static boolean SHOW_VERBOSE = true;
    public static boolean SHOW_INFO = true;
    public static boolean SHOW_WARN = true;
    public static boolean SHOW_ERROR = true;
    
    public static void D(String TAG, String info) {
        // ...
    }
    
    public static void V(String TAG, String info) {
        // ...
    }
    
    public static void I(String TAG, String info) {
        // ...
    }
    
    public static void W(String TAG, String info) {
        // ...
    }
    
    public static void E(String TAG, String info) {
        // ...
    }
}
```

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## 作者
- [Joshua Yin](https://github.com/joshuayin)

## 组织
- [UCloud](https://github.com/ucloud)
