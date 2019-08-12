# UCloud NetAnalysis SDK for Android

[![](https://img.shields.io/github/release/ucloud/netanalysis-sdk-android.svg)](https://github.com/ucloud/netanalysis-sdk-android)
[![](https://img.shields.io/github/last-commit/ucloud/netanalysis-sdk-android.svg)](https://github.com/ucloud/netanalysis-sdk-android)
[![](https://img.shields.io/github/commits-since/ucloud/netanalysis-sdk-android/latest.svg)](https://github.com/ucloud/netanalysis-sdk-android)

## 运行环境
### Android
- Android系统版本：**4.1** (API 16)及以上

</br></br>

## 使用
### makeJar
* 在`SDK项目根目录/UNetAnalysisLib/build.gradle` 中有`makeJar`和`makeProguardJar`两个task，分别是编译普通jar和混淆后的jar
* 编译任务完成后，jar包会存放在`SDK项目根目录/UNetAnalysisLib/build/libs/`下，`proguard-UNetAnalysisLib.jar`是混淆包，`UNetAnalysisLib.jar`是普通包
* 图解：

    ![avatar](http://esl-ipdd-res.cn-sh2.ufileos.com/WX20190306-155134.png)
    ![avatar](http://esl-ipdd-res.cn-sh2.ufileos.com/WX20190306-155422.png)

### Dependencies
- 将NetAnalysisLib.jar放入项目app模块中的libs目录下，并在app模块的build.gradle的dependencies中建立依赖

</br>

### AndroidManifest配置
以下是NetAnalysis SDK所需要的Android权限，请确保您的AndroidManifest.xml文件中已经配置了这些权限，否则，SDK将无法正常工作。

- 权限

    ``` xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    ```

- application

    ``` xml
    <application
            ...
            // 如果targetSdkVersion>= 28,则需要配置usesCleartextTraffic: true
            android:usesCleartextTraffic="true">
            ...
    </application>
    ```

</br>

### Proguard

> 如果您的项目最终需要混淆编译，那么请参考UNetAnalysisLib模块下的[proguard-rules.pro](https://github.com/ucloud/netanalysis-sdk-android/blob/master/UNetAnalysisLib/proguard-rules.pro)文件

- 主要内容：

    ``` proguard
    # com.ucloud.library.netanalysislib.**
    -keep class com.ucloud.library.netanalysis.** {
        public <fields>;
        public <methods>;
    }
    
    # -------------------------------------------------------------
    # Also keep - Enumerations. Keep the special static methods that are required in
    # enumeration classes.
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }
    ```

</br></br>

### 快速接入
#### 1、初始化SDK模块

``` java
// 使用Application Context 初始化 UmqaClient
String appKey = "UCloud为您的APP分配的APP_KEY";
String appSecret = "UCloud为您的APP分配的APP_SECRET";

// 初始化SDK模块
boolean result = UmqaClient.init(getApplicationContext(), appKey, appSecret);
// result = true : 初始化成功; false : 表示重复初始化，需要先调用UmqaClient.destroy()
```

#### 2、配置信息

``` java
// 配置待检测的用户自定义IP
List<String> ips = new ArrayList();
ips.add("8.8.8.8");

// 不支持填写域名，请填写IP地址
UmqaClient.setCustomIps(ips);

// 用户自定义上报字段，字段为String-String的键值对Map
UserDefinedData.Builder builder = new UserDefinedData.Builder();
// 或者：UserDefinedData.Builder builder = new UserDefinedData.Builder(Map<String, String>);
builder.addParam(new UserDefinedData.UserDefinedParam("id", sb.toString()));

/**
 * 所有的自定义字段将会以以下JSON数据的最小化字符串形式上报，转换成字符串后的最大长度为 1024 Byte。
 * 超长将会在create()时抛出异常
 * [
 *     {"key":"your key1","val":"your value1"},
 *     {"key":"your key2","val":"your value2"},
 *     ...
 * ]
 */
try {
    UserDefinedData param = builder.create();
    UmqaClient.setUserDefinedData(param);
} catch (UCParamVerifyException e) {
    e.printStackTrace();
}
```

#### 3、注册启用SDK，建议在**自定义Application类**或者**主Activity类**的onCreate中调用

##### 注意事项：
- 若使用默认UCConfig进行register，（即：调用register(OnSdkListener listener)，或者手动声明new UCConfig(LogLevel, **isAutoDetect = true**)）, 表示允许SDK**自动检测**。
- **自动检测** 会在以下时机进行检测IP：
    - register之后，自动检测UCloud数据中心IP（最近的5个），若有CustomIPs则也会加入检测队列检测
    - 网络发生改变后，自动检测UCloud数据中心IP（最近的5个），若有CustomIPs则也会加入检测队列检测
    - setCustomIps()之后，自动将CustomIPs加入检测队列检测
- 如果允许自动检测，并希望检测结果上报都包含自定义字段，**那么需要在register之前，先调用UmqaClient.setUserDefinedData()**，否则可能出现部分自动检测上报的数据来不及取得用户自定义上报字段
- 如果不允许自动检测，则只有当用户手动调用UmqaClient.analyse()接口时才会检测并上报

``` java
// SDK回调，异步回调，非主线程
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

/**
 * 定义设置项
 * new UCConfig() : 默认值：LogLevel.RELEASE, 开启自动检测
 */
UCConfig config = new UCConfig(UCConfig.LogLevel.DEBUG, true);

/**
 * 注册SDK，开始监听网络，开启检测功能
 * 两种register方式可选：
 * 1、register(OnSdkListener listener)，使用默认值UCConfig
 * 2、register(OnSdkListener listener, UCConfig config)，使用自定义UCConfig
 */
boolean result = UmqaClient.register(this, config);
// result = true : register成功; false : register失败，没有init
```


#### 4、在你的应用退出时，注销并销毁 UmqaClient

``` java
@Override
protected void onDestroy(){
    // 注销SDK模块，注销后将停止一切未完成的操作
    UmqaClient.unregister();
    // 销毁SDK实例以释放内存空间
    UmqaClient.destroy();
    super.onDestroy();
}
```

</br></br>

## 类
### UmqaClient
> UmqaClient是UMQA产品移动网络探测SDK部分的主要类，一切本地API都只需要在UmqaClient中操作调用

</br>
#### 初始化UmqaClient
- 若appKey和appSecret不符合规则，则会抛出**IllegalArgumentException**

``` java
public static synchronized boolean init(@NonNull Context applicationContext, @NonNull String appKey, @NonNull String appSecret)
```
- **param**:
    -  applicationContext: application的context
    -  appKey: UCloud为您的APP分配的APP_KEY
    -  appSecret: UCloud为您的APP分配的APP_SECRET
- **return**: 是否init成功，若重复init，则返回false，需要先destroy后重新init

</br>
#### 注册UmqaClient

``` java
public synchronized static boolean register(OnSdkListener listener) 
```

- **param**: 
    - listener: OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: 是否register成功，若没有init，则返回false

</br>
#### 注册UmqaClient (带有自定义配置项)
``` java
public synchronized static boolean register(OnSdkListener listener, UCConfig config)
```

- **param**: 
    - listener: OnSdkListener回调接口，详情见**OnSdkListener**说明
    - config: 自定义配置项，详情见**UCConfig**说明
- **return**: 是否register成功，若没有init，则返回false

</br>
#### 设置SDK回调接口
``` java
public synchronized static boolean setSdkListener(OnSdkListener listener)
```

- **param**: 
    - listener:  OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: 是否设置成功，若没有init，则返回false

</br>
#### 设置自定义的IP列表
- 最多5个IP，多于5个的，自动取前5个 
- 不支持填写域名，请填写IP地址

``` java
public synchronized static boolean setCustomIps(List<String> custonIps)
```

- **param**: 
    - custonIps:  自定义的IP列表
- **return**: 是否设置成功，若没有init，则返回false

</br>
#### 获取已设置的自定义的IP地址
``` java
public synchronized static List<String> getCustomIps()
```

- **param**: -
- **return**: 
    - List<String>:  已设置的自定义IP列表，**null**: UmqaClient未init

</br>
#### 设置自定义上报字段
``` java
public synchronized static boolean setUserDefinedData(UserDefinedData userDefinedData)
```

- **param**: 
    - userDefinedData: 自定义上报字段，详情见**UserDefinedData**
- **return**: 是否设置成功，若没有init，则返回false

</br>
#### 手动触发网络检测
``` java
public synchronized static boolean analyse()
```

- **param**: -
- **return**: 是否触发成功，若没有init或者没有register，则返回false

</br>
#### 检查当前设备网络状态
``` java
public synchronized static UCNetworkInfo checkNetworkStatus()
```

- **param**: -
- **return**: 
    - UCNetworkInfo:  当前设备的网络状态，详情见**UCNetworkInfo**，**null** 表示可能UmqaClient未init

</br>
#### 注销UmqaClient模块
``` java
public synchronized static boolean unregister()
```

- **param**: -
- **return**: 是否unregister成功，若没有init，则返回false

</br>
#### 销毁UmqaClient模块
``` java
public synchronized static void destroy()
```

- **param**: -
- **return**: -

</br></br>
### UCConfig
> 设置选项

``` java
public class UCConfig {
    
    public enum LogLevel {
        TEST,
        DEBUG,
        RELEASE
    }
    
    /**
     * @param logLevel     Log级别，default = {@link UCConfig.LogLevel.RELEASE}
     * @param isAutoDetect 是否开启自动检测, default = true
     */
    public UCConfig(LogLevel logLevel, boolean isAutoDetect) {
        ...
    }
    
    /**
     * 构造方法
     *
     * @param isAutoDetect 是否开启自动检测, default = true
     */
    public UCConfig(boolean isAutoDetect) {
        ...
    }
    
    /**
     * 构造方法
     *
     * @param logLevel Log级别，default = {@link UCConfig.LogLevel.RELEASE}
     */
    public UCConfig(LogLevel logLevel) {
        ...
    }
    
    /**
     * 构造方法
     * 
     * LogLevel = {@link UCConfig.LogLevel.RELEASE}
     * isAutoDetect = true
     */
    public UCConfig() {
        ...
    }
}
```

</br></br>
### UserDefinedData
> 用户可选的自定义上报字段

``` java 
public class UserDefinedData {
        
    public static class Builder {
        // 无参构造
        public Builder(){}
        // 预设自定义字段的Map
        public Builder(Map<String, String> map){}
        // put字段进Map，若先put，再setData则会覆盖
        public Builder putParam(UserDefinedParam param){}
        // 设置预设自定义字段Map
        public void setData(Map<String, String> map){}
        // 创建UserDefinedData
        public UserDefinedData create() throws UCParamVerifyException {}
    }
    
    public static class UserDefinedParam {
        public UserDefinedParam(String key, String value) {}
    }
}
```

### 注意事项
### UCloud尊重客户和终端用户的隐私，请务必不要上传带有用户隐私信息，包括但不限于：用户的姓名、手机号、身份证号、手机IMEI值、地址等敏感信息
- UserDefinedData是(String-String)键值对Map，其中Key不能是null或者""。
- 如果有不满足规则的UserDefinedData，`UserDefinedData.Builder create()`时会抛出`UCParamVerifyException`，具体错误信息，可以通过异常的getMessage()获取。
- 该字段作为用户在查询上报数据时，可作为查询索引，故**不建议用户在Value中拼接多个值**。
- 所有的自定义字段将会以以下JSON的最小化字符串形式上报，转换成字符串后的**最大长度为1024Byte**。
  超长将会在`UserDefinedData.Builder create()`时抛出异常
  
    ``` json
    [
        {"key": "your key1", "val": "your value1"},
        {"key": "your key2", "val": "your value2"},
        ...
    ]
    ```


</br></br>
### OnSdkListener
> UmqaClient模块回调接口，**非主线程异步回调**

``` java
public interface OnSdkListener {
    // 注册结果回调，status详情见UCSdkStatus
    void onRegister(UCSdkStatus status);
 
    // 网络情况改变回调，status详情见UCNetworkInfo
    void onNetworkStatusChanged(UCNetworkInfo status);
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
    /**
     * 注册模块成功
     */
    REGISTER_SUCCESS,
    /**
     * 已注册过SDK
     */
    SDK_HAS_BEEN_REGISTERED,
    /**
     * 正在注册SDK中
     */
    SDK_IS_REGISTING,
    /**
     * SDK获取授权失败
     */
    OBTAIN_AUTH_FAILED,
    /**
     * SDK被远程关闭
     */
    SDK_IS_CLOSED_BY_REMOTE
}
```


</br></br>
### UCNetStatus
> 网络状态集

``` java
public enum UCNetStatus {
    // 无网络连接
    NET_STATUS_NOT_REACHABLE,
 
    // WIFI网络
    NET_STATUS_WIFI,
 
    // 4G网络
    NET_STATUS_4G,
 
    // 3G网络
    NET_STATUS_3G,
 
    // 2G网络
    NET_STATUS_2G,
 
    // 未知类型
    NET_STATUS_UNKNOW,
}
```

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## 作者
- [Joshua Yin](https://github.com/joshuayin)

## 组织
- [UCloud](https://github.com/ucloud)
