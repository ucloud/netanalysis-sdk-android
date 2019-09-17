package com.ucloud.demo.netanalysis;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucloud.library.netanalysis.UmqaClient;
import com.ucloud.library.netanalysis.callback.OnSdkListener;
import com.ucloud.library.netanalysis.exception.UCParamVerifyException;
import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UCSdkStatus;
import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.utils.UCConfig;
import com.ucloud.library.netanalysis.utils.JLog;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnSdkListener {
    private final String TAG = getClass().getSimpleName();
    private volatile WeakReference<Handler> mWeakHandler;
    private ScrollView srcollview;
    private TextView txt_result;
    
    private String appKey = UCloud为您的APP分配的APP_KEY;
    private String appSecret = UCloud为您的APP分配的APP_SECRET;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化SDK模块
        UmqaClient.init(getApplicationContext(), appKey, appSecret);
        
        srcollview = findViewById(R.id.srcollview);
        txt_result = findViewById(R.id.txt_result);
        
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_unregister).setOnClickListener(this);
        findViewById(R.id.btn_set_ips).setOnClickListener(this);
        findViewById(R.id.btn_get_ips).setOnClickListener(this);
        findViewById(R.id.btn_set_user_defined).setOnClickListener(this);
        findViewById(R.id.btn_analyse).setOnClickListener(this);
        findViewById(R.id.btn_net_status).setOnClickListener(this);
    }
    
    private synchronized Handler getHandler() {
        if (mWeakHandler == null || mWeakHandler.get() == null) {
            mWeakHandler = new WeakReference<Handler>(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg == null)
                        return;
                    
                    Bundle bd = msg.getData();
                    String info;
                    if (bd == null || (info = bd.getString("msg", null)) == null)
                        return;
                    
                    StringBuilder sb = new StringBuilder(info).append("\n\n");
                    String histroy = txt_result.getText() == null ? "" : txt_result.getText().toString().trim();
                    sb.append(histroy);
                    txt_result.setText(sb.toString());
                    srcollview.smoothScrollTo(0, 0);
                }
            });
        }
        
        return mWeakHandler.get();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        UmqaClient.unregister();
        UmqaClient.destroy();
        super.onDestroy();
    }
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: {
                new ConfigDialog(new ConfigDialog.OnConfigListener() {
                    @Override
                    public void onCommitConfig(UCConfig config) {
                        boolean res = UmqaClient.register(MainActivity.this, config);
                        Message msg = new Message();
                        Bundle bd = new Bundle();
                        bd.putString("msg", String.format("[%s]: register -> %b", dateFormat.format(
                                new Date(System.currentTimeMillis())), res));
                        msg.setData(bd);
                        getHandler().sendMessage(msg);
                    }
                }, ((DemoApplication) getApplication()).getSharedPreferences()).show(getSupportFragmentManager(), "ConfigDialog");
                
                break;
            }
            case R.id.btn_unregister: {
                boolean res = UmqaClient.unregister();
                Message msg = new Message();
                Bundle bd = new Bundle();
                bd.putString("msg", String.format("[%s]: unregister -> %b", dateFormat.format(
                        new Date(System.currentTimeMillis())), res));
                msg.setData(bd);
                getHandler().sendMessage(msg);
                break;
            }
            case R.id.btn_set_ips: {
                startActivityForResult(CustomIpActivity.startAction(this), CustomIpActivity.REQUEST_CODE);
                break;
            }
            case R.id.btn_get_ips: {
                List<String> ips = UmqaClient.getCustomIps();
                Message msg = new Message();
                Bundle bd = new Bundle();
                StringBuilder sb = new StringBuilder(String.format("[%s]: get custom IPs -> ", dateFormat.format(
                        new Date(System.currentTimeMillis()))));
                if (ips == null || ips.isEmpty()) {
                    sb.append("Empty");
                } else {
                    for (String ip : ips) {
                        sb.append("\n\t\t");
                        sb.append(ip);
                    }
                }
                bd.putString("msg", sb.toString());
                msg.setData(bd);
                getHandler().sendMessage(msg);
                break;
            }
            case R.id.btn_set_user_defined: {
                startActivityForResult(UserDefinedDataActivity.startAction(this), UserDefinedDataActivity.REQUEST_CODE);
                break;
            }
            case R.id.btn_analyse: {
                boolean res = UmqaClient.analyse();
                Message msg = new Message();
                Bundle bd = new Bundle();
                bd.putString("msg", String.format("[%s]: detect -> %b", dateFormat.format(
                        new Date(System.currentTimeMillis())), res));
                msg.setData(bd);
                getHandler().sendMessage(msg);
                
                break;
            }
            case R.id.btn_net_status: {
                UCNetworkInfo networkInfo = UmqaClient.checkNetworkStatus();
                Message msg = new Message();
                Bundle bd = new Bundle();
                bd.putString("msg", String.format("[%s]: net status -> \n\t\t%s", dateFormat.format(
                        new Date(System.currentTimeMillis())), networkInfo == null ? "null" : networkInfo.toString()));
                msg.setData(bd);
                getHandler().sendMessage(msg);
                break;
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CustomIpActivity.REQUEST_CODE: {
                if (resultCode != RESULT_OK)
                    return;
                
                if (data == null)
                    return;
                
                List<String> customIPs = data.getStringArrayListExtra("custom_ips");
                
                boolean res = UmqaClient.setCustomIps(customIPs);
                Message msg = new Message();
                Bundle bd = new Bundle();
                bd.putString("msg", String.format("[%s]: set custom IPs -> %b", dateFormat.format(
                        new Date(System.currentTimeMillis())), res));
                msg.setData(bd);
                getHandler().sendMessage(msg);
                return;
            }
            case UserDefinedDataActivity.REQUEST_CODE: {
                if (resultCode != RESULT_OK)
                    return;
                
                if (data == null)
                    return;
                
                String json = data.getStringExtra("user_defined_data");
                UserDefinedData.Builder builder = new UserDefinedData.Builder();
                try {
                    JSONObject jobj = new JSONObject(json);
                    Iterator<String> iterator = jobj.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        builder.putParam(new UserDefinedData.UserDefinedParam(key, jobj.optString(key, null)));
                    }
                    UserDefinedData userDefinedData = builder.create();
                    boolean res = UmqaClient.setUserDefinedData(userDefinedData);
                    Message msg = new Message();
                    Bundle bd = new Bundle();
                    StringBuilder sb = new StringBuilder(String.format("[%s]: set user defined data -> %b", dateFormat.format(
                            new Date(System.currentTimeMillis())), res));
                    sb.append("\n\t\t");
                    sb.append(userDefinedData.toString());
                    bd.putString("msg", sb.toString());
                    msg.setData(bd);
                    getHandler().sendMessage(msg);
                } catch (JSONException | UCParamVerifyException e) {
                    e.printStackTrace();
                }
                
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onRegister(UCSdkStatus status) {
        JLog.I(TAG, "onRegister---> " + status.name());
        Message msg = new Message();
        Bundle bd = new Bundle();
        bd.putString("msg", String.format("[%s]: onRegister -> %s", dateFormat.format(
                new Date(System.currentTimeMillis())), status.name()));
        msg.setData(bd);
        getHandler().sendMessage(msg);
    }
    
    @Override
    public void onNetworkStatusChanged(UCNetworkInfo networkInfo) {
        JLog.I(TAG, "onNetworkStatusChanged---> " + networkInfo.toString());
        Message msg = new Message();
        Bundle bd = new Bundle();
        bd.putString("msg", String.format("[%s]: net change -> \n\t\t%s", dateFormat.format(
                new Date(System.currentTimeMillis())), networkInfo.toString()));
        msg.setData(bd);
        getHandler().sendMessage(msg);
    }
    
    private Long firstClickBack = new Long(0);
    private Timer timer = new Timer("click to quit", false);
    
    private class ClickTask extends TimerTask {
        @Override
        public void run() {
            synchronized (firstClickBack) {
                firstClickBack = 0l;
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        synchronized (firstClickBack) {
            if (firstClickBack.longValue() == 0l) {
                firstClickBack = SystemClock.elapsedRealtime();
                Toast.makeText(this, "Click again to quit", Toast.LENGTH_SHORT).show();
                timer.schedule(new ClickTask(), 1500);
                return;
            }
        }
        
        super.onBackPressed();
    }
}