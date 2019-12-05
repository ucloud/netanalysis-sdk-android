package com.ucloud.demo.netanalysis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by joshua on 2019-08-03 13:10.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class CustomIpActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    public static final int REQUEST_CODE = 2000;
    
    private List<String> customIPs;
    private SharedPreferences mSharedPreferences;
    
    private AppCompatEditText[] edits = new AppCompatEditText[5];
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ip);
        
        setTitle("Edit Custom IPs");
        
        mSharedPreferences = ((DemoApplication) getApplication()).getSharedPreferences();
        customIPs = new ArrayList<>(5);
        
        edits[0] = findViewById(R.id.edit_ip_1);
        edits[1] = findViewById(R.id.edit_ip_2);
        edits[2] = findViewById(R.id.edit_ip_3);
        edits[3] = findViewById(R.id.edit_ip_4);
        edits[4] = findViewById(R.id.edit_ip_5);
        
        findViewById(R.id.txt_submit).setOnClickListener(this);
        
        Set<String> ipSet = mSharedPreferences.getStringSet("custom_ips", null);
        if (ipSet != null) {
            int count = 0;
            for (String ip : ipSet) {
                if (count > 4)
                    break;
                if (TextUtils.isEmpty(ip))
                    continue;
                customIPs.add(ip);
                edits[count++].setText(ip);
            }
        }
    }
    
    public static Intent startAction(Context context) {
        return new Intent(context, CustomIpActivity.class);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_submit: {
                Set<String> ipSet = new ArraySet<>();
                customIPs.clear();
                for (AppCompatEditText edit : edits) {
                    String ip;
                    if (edit.getText() == null || TextUtils.isEmpty(ip = edit.getText().toString().trim()))
                        continue;
                    
                    ipSet.add(ip);
                    customIPs.add(ip);
                }
                mSharedPreferences.edit()
                        .putStringSet("custom_ips", ipSet)
                        .apply();
                
                Intent result = new Intent();
                result.putStringArrayListExtra("custom_ips", (ArrayList<String>) customIPs);
                setResult(RESULT_OK, result);
                finish();
                
                break;
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
