package com.ucloud.library.netanalysis;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Set;


/**
 * Created by Joshua_Yin on 2017/12/26 22:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */

class UCSharedPreferenceHolder {
    private final String SUB_SHARED_PREFERENCE_NAME = "UNetAnalysisSDK";
    private WeakReference<USharedPreferences> mWeakRefSP;
    private static volatile UCSharedPreferenceHolder mHolder = null;
    
    private Context context;
    
    private UCSharedPreferenceHolder(Context context) {
        this.context = context;
    }
    
    static UCSharedPreferenceHolder createHolder(@NonNull Context context) {
        synchronized (UCSharedPreferenceHolder.class) {
            if (mHolder == null)
                mHolder = new UCSharedPreferenceHolder(context);
        }
        
        return mHolder;
    }
    
    synchronized static UCSharedPreferenceHolder getHolder() {
        return mHolder;
    }
    
    synchronized USharedPreferences getSharedPreferences() {
        if (mWeakRefSP == null || mWeakRefSP.get() == null)
            mWeakRefSP = new WeakReference<>(new USharedPreferences(context, context.getPackageName() + "_" + SUB_SHARED_PREFERENCE_NAME));
        
        return mWeakRefSP.get();
    }
    
    static class USharedPreferences {
        private SharedPreferences mSharedPreferences;
        
        private USharedPreferences(@NonNull Context context, @NonNull String name) {
            mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        
        SharedPreferences.Editor edit() {
            return mSharedPreferences.edit();
        }
        
        int getInt(@NonNull String key, int def) {
            return mSharedPreferences.getInt(key, def);
        }
        
        float getFloat(@NonNull String key, float def) {
            return mSharedPreferences.getFloat(key, def);
        }
        
        long getLong(@NonNull String key, long def) {
            return mSharedPreferences.getLong(key, def);
        }
        
        boolean getBoolean(@NonNull String key, boolean def) {
            return mSharedPreferences.getBoolean(key, def);
        }
        
        String getString(@NonNull String key, String def) {
            return mSharedPreferences.getString(key, def);
        }
        
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        Set<String> getStringSet(@NonNull String key, Set<String> def) {
            return mSharedPreferences.getStringSet(key, def);
        }
    }
}
