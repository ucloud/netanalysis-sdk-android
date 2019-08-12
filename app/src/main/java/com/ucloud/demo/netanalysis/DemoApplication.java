package com.ucloud.demo.netanalysis;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by joshua on 2019-08-03 15:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class DemoApplication extends Application {
    
    private SharedPreferences mSharedPreferences;
    
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            }
        }.start();
    }
    
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
