package com.ucloud.demo.netanalysis;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.ucloud.library.netanalysis.utils.UCConfig;

/**
 * Created by joshua on 2019-08-02 16:46.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
@SuppressLint("ValidFragment")
public class ConfigDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
    private final String TAG = getClass().getSimpleName();
    
    private UCConfig config;
    
    public interface OnConfigListener {
        void onCommitConfig(UCConfig config);
    }
    
    private OnConfigListener listener;
    private SharedPreferences sharedPreferences;
    
    @SuppressLint("ValidFragment")
    public ConfigDialog(OnConfigListener listener, SharedPreferences sharedPreferences) {
        this.listener = listener;
        this.sharedPreferences = sharedPreferences;
        config = new UCConfig(UCConfig.LogLevel.values()[sharedPreferences.getInt("log_level", 0)],
                sharedPreferences.getBoolean("is_auto_detect", true));
    }
    
    public void setListener(OnConfigListener listener) {
        this.listener = listener;
    }
    
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_config, null);
        AppCompatSpinner spinner = view.findViewById(R.id.spinner_log_lv);
        AppCompatCheckBox checkBox = view.findViewById(R.id.checkbox_auto);
        
        spinner.setSelection(config.getLogLevel().ordinal());
        checkBox.setChecked(config.isAutoDetect());
        
        checkBox.setOnCheckedChangeListener(this);
        spinner.setOnItemSelectedListener(this);
        
        builder.setView(view).setPositiveButton("Commit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedPreferences.edit()
                        .putInt("log_level", config.getLogLevel().ordinal())
                        .putBoolean("is_auto_detect", config.isAutoDetect())
                        .apply();
                
                if (listener != null)
                    listener.onCommitConfig(config);
                
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true);
        
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        
        return dialog;
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        config.setAutoDetect(isChecked);
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        config.setLogLevel(UCConfig.LogLevel.values()[position]);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    
    }
}
