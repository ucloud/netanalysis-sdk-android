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
* 在`SDK项目根目录`/UNetAnalysisLib/build.gradle 中有`makeJar`和`makeProguardJar`两个task，分别是编译普通jar和混淆后的jar
* 图解：

    ![avatar](http://esl-ipdd-res.cn-sh2.ufileos.com/WX20190306-155134.png)
    ![avatar](http://esl-ipdd-res.cn-sh2.ufileos.com/WX20190306-155422.png)

### Dependencies
- 将NetAnalysisLib.jar放入项目app模块中的libs目录下，并在app模块的build.gradle的dependencies中建立依赖

</br>



### 系统权限设置
以下是NetAnalysis SDK所需要的Android权限，请确保您的AndroidManifest.xml文件中已经配置了这些权限，否则，SDK将无法正常工作。
``` xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
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
#### 1、在**自定义Application类**或者**主Activity类**的onCreate中构建UCNetAnalysisManager，并注册
``` java
// 使用Application Context 构建UCNetAnalysisManager实例
String appKey = "UCloud为您的APP分配的APP_KEY";
String appSecret = "UCloud为您的APP分配的APP_SECRET";

/**
 * 定义设置项
 * new UCConfig() : 默认LogLevel.RELEASE
 */
UCConfig config = new UCConfig(UCConfig.LogLevel.DEBUG);

UCNetAnalysisManager manager = UCNetAnalysisManager.createManager(context.getApplicationContext(), appKey, appSecret, config);

// or: 使用默认new UCConfig(), Log级别RELEASE
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

// (可选) - 用户自定义上报字段，字段为String-String的键值对Map
UserDefinedData.Builder builder = new UserDefinedData.Builder();
// or 
// UserDefinedData.Builder builder = new UserDefinedData.Builder(Map<String, String>);
builder.addParam(new UserDefinedData.UserDefinedParam("id", sb.toString()));
UserDefinedData param = null;
/**
 * 所有的自定义字段将会以以下JSON的最小化字符串形式上报，转换成字符串后的最大长度为 1024 Byte。
 * 超长将会抛出异常
 * [
 *      {
 *          "key": "",
 *          "val": ""
 *      },
 *      ...
 * ]
 */
try {
    param = builder.create();
} catch (UCParamVerifyException e) {
    e.printStackTrace();
}

/**
 * 注册sdk模块，注册成功之后，若当前有可用网络，即会自动开始检测网络质量
 * 注意：
 *   manager.register(listener, null) == manager.register(listener)
 */
manager.register(sdkListener, param); // 配置自定义上报字段的注册

```


#### 2、配置你需要分析网络质量的IP地址或者域名
``` java
List<String> ips = new ArrayList();
ips.add("127.0.0.1");
ips.add("127.0.0.2");

/*
 * 配置你想要监测的网络质量的ip，配置后即加入自动检测的队列，无需触发
 * 注意：不支持填写域名，请填写IP地址
 */
manager.setCustomIps(ips);
```


#### 3、在你的应用退出时，注销并销毁UCNetAnalysisManager
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

#### 创建默认Config的UCNetAnalysisManager单例对象
``` java
public static UCNetAnalysisManager createManager(Context applicationContext, String appKey, String appSecret)
```
- **param**:
    -  applicationContext: application的context
    -  appKey: UCloud为您的APP分配的APP_KEY
    -  appSecret: UCloud为您的APP分配的APP_SECRET
- **return**: UCNetAnalysisManager单例对象

#### 创建自定义Config的UCNetAnalysisManager单例对象
``` java
public static UCNetAnalysisManager createManager(Context applicationContext, String appKey, String appSecret, UCConfig config)
```
- **param**:
    -  applicationContext: application的context
    -  appKey: UCloud为您的APP分配的APP_KEY
    -  appSecret: UCloud为您的APP分配的APP_SECRET
    -  config: 设置选项，详情见**UCConfig**说明
- **return**: UCNetAnalysisManager单例对象

#### 获取UCNetAnalysisManager单例对象
``` java
public static UCNetAnalysisManager getManager()
```

- **param**: -
- **return**: UCNetAnalysisManager单例对象，若未调用createManager，则return为null

#### 注册UCNetAnalysisManager模块
- 注册成功后，若当前存在可用网络，将会自动开始检测网络质量

``` java
public void register(OnSdkListener listener)
```

- **param**: 
    - listener: OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: -

#### 注册UCNetAnalysisManager模块(带有用户自定义上报字段)
``` java
public void register(OnSdkListener listener, UserDefinedData userDefinedData)
```

- **param**: 
    - listener: OnSdkListener回调接口，详情见**OnSdkListener**说明
    - userDefinedData: 用户自定义上报字段，详情见**UserDefinedData**说明
- **return**: -


#### 设置Sdk回调接口
``` java
public void setSdkListener(OnSdkListener listener)
```

- **param**: 
    - listener:  OnSdkListener回调接口，详情见**OnSdkListener**说明
- **return**: -

#### 设置自定义的IP地址
``` java
public void setCustomIps(List<String> ips)
```

##### 注意：不支持填写域名，请填写IP地址

- **param**: 
    - ips:  自定义需要网络分析的IP地址列表
- **return**: -

#### 获取已设置的自定义的IP地址
``` java
public List<String> getCustomIps()
```

- **param**: -
- **return**: 
    - List<String>:  已设置的需要网络分析的IP地址或域名列表

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
- 如果有不满足规则的UserDefinedData，UserDefinedData.Builder.create()时会抛出UCParamVerifyException，具体错误信息，可以通过异常的getMessage()获取。
- 该字段作为用户在查询上报数据时，可作为查询索引，故**不建议用户在Value中拼接多个值**。
- 所有的自定义字段将会以以下JSON的最小化字符串形式上报，转换成字符串后的**最大长度为1024Byte**。
  超长将会抛出异常
  
    ``` json
    [
        {
            "key": "",
            "val": ""
        },
        ...
    ]
    ```


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
    
    // 配置LogLevel
    public UCConfig(UCConfig.LogLevel logLevel) {
        // 构造方法
    }

    // 默认 LogLevel.RELEASE
    public UCConfig() {
        // 构造方法
    }
}
```

## License
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## 作者
- [Joshua Yin](https://github.com/joshuayin)

## 组织
- [UCloud](https://github.com/ucloud)
