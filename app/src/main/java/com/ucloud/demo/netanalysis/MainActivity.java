package com.ucloud.demo.netanalysis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ucloud.library.netanalysis.UCNetAnalysisManager;
import com.ucloud.library.netanalysis.callback.OnAnalyseListener;
import com.ucloud.library.netanalysis.callback.OnSdkListener;
import com.ucloud.library.netanalysis.module.UCAnalysisResult;
import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UCSdkStatus;
import com.ucloud.library.netanalysis.utils.JLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnSdkListener {
    private volatile WeakReference<Handler> mWeakHandler;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mAlertBuilder;
    
    static {
        JLog.SHOW_DEBUG = true;
    }
    
    private UCNetAnalysisManager mUCNetAnalysisManager;
    
    private TextView txt_result;
    private AppCompatEditText edit_host;
    private InputMethodManager imm;
    
    private String appKey = "41bb155d-f067-5215-b496-252e30997247";
    private String appSecret = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCY+RM+TrTHN9Ubus5Mvro4bmJw\nP+jP0QAJchxnukisrl6JwxiVWQk77WDV5Bizs1vXf3nqsLo3L4L1mXf5u/vqAWKQ\n+k9FsuWm9/xZrOpqGpENh6pI1OKjTdTLkvNykgZJOZ5vllHnZUxTWbUHZxeMwNdP\nfmLRx99uPb1P8Vxz+QIDAQAB\n-----END PUBLIC KEY-----";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUCNetAnalysisManager = UCNetAnalysisManager.createManager(getApplicationContext(), appKey, appSecret);
    
        /**
         * 可以配置自定义需要检测的域名或IP地址
         */
        List<String> ips = new ArrayList<>();
        ips.add("www.ucloud.cn");
        ips.add("www.github.com");
        ips.add("14.215.177.38");
        mUCNetAnalysisManager.setCustomIps(ips);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
        txt_result = findViewById(R.id.txt_result);
        edit_host = findViewById(R.id.edit_host);
        mAlertBuilder = new AlertDialog.Builder(this);
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Waiting...");
        
        mAlertBuilder.setTitle("Error").setMessage("请输入合法的Host")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false);
    
        findViewById(R.id.btn_set_ips).setOnClickListener(this);
        findViewById(R.id.btn_analyse).setOnClickListener(this);
        findViewById(R.id.btn_net_status).setOnClickListener(this);
        
        List<String> list = mUCNetAnalysisManager.getCustomIps();
        StringBuffer sb = new StringBuffer();
        if (list != null && !list.isEmpty())
            for (String ip : list)
                if (!TextUtils.isEmpty(ip))
                    sb.append(ip + "\n");
        
        edit_host.setText(sb.toString().trim());
    }
    
    private synchronized Handler getHandler() {
        if (mWeakHandler == null || mWeakHandler.get() == null) {
            mWeakHandler = new WeakReference<>(new Handler(Looper.getMainLooper()));
        }
        
        return mWeakHandler.get();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mUCNetAnalysisManager.register(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mUCNetAnalysisManager.unregister();
    }
    
    @Override
    protected void onDestroy() {
        UCNetAnalysisManager.destroy();
        super.onDestroy();
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_analyse: {
                edit_host.clearFocus();
                txt_result.setText("");
                mProgressDialog.show();
                mUCNetAnalysisManager.analyse(new OnAnalyseListener() {
                    @Override
                    public void onAnalysed(final UCAnalysisResult result) {
                        JLog.E("TEST", result.toString());
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                try {
                                    txt_result.setText(new JSONObject(result.toString()).toString(4));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                
                break;
            }
            case R.id.btn_set_ips: {
                edit_host.clearFocus();
                txt_result.setText("");
                String host = edit_host.getText().toString().trim();
                if (TextUtils.isEmpty(host)) {
                    mAlertBuilder.create().show();
                    return;
                }
                
                List<String> ips = new ArrayList<>();
                String[] cache = host.split("\n");
                for (String str : cache) {
                    if (!TextUtils.isEmpty(str))
                        ips.add(str);
                }
                mUCNetAnalysisManager.setCustomIps(ips);
                Toast.makeText(this, "配置成功", Toast.LENGTH_SHORT).show();
                
                break;
            }
            case R.id.btn_net_status: {
                UCNetworkInfo networkInfo = mUCNetAnalysisManager.checkNetworkStatus();
                txt_result.setText(networkInfo == null ? "null" : networkInfo.toString());
                break;
            }
        }
    }
    
    @Override
    public void onRegister(UCSdkStatus status) {
        Toast.makeText(this, status.name(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onNetworkStatusChanged(UCNetworkInfo networkInfo) {
        Toast.makeText(this, networkInfo.toString(), Toast.LENGTH_SHORT).show();
    }
}