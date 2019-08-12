package com.ucloud.demo.netanalysis;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.utils.UCConfig;

/**
 * Created by joshua on 2019-08-02 16:46.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
@SuppressLint("ValidFragment")
public class EditDataDialog extends AppCompatDialogFragment {
    private final String TAG = getClass().getSimpleName();
    
    private UserDefinedData.UserDefinedParam param;
    
    public interface OnEditDataListener {
        void onCommit(UserDefinedData.UserDefinedParam param);
    }
    
    private OnEditDataListener listener;
    
    @SuppressLint("ValidFragment")
    public EditDataDialog(OnEditDataListener listener) {
        this(listener, new UserDefinedData.UserDefinedParam(null, null));
    }
    
    @SuppressLint("ValidFragment")
    public EditDataDialog(OnEditDataListener listener, UserDefinedData.UserDefinedParam param) {
        this.listener = listener;
        this.param = param == null ? new UserDefinedData.UserDefinedParam(null, null) : param;
    }
    
    public void setListener(OnEditDataListener listener) {
        this.listener = listener;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_data, null);
        final AppCompatEditText edit_key = view.findViewById(R.id.edit_key);
        final AppCompatEditText edit_val = view.findViewById(R.id.edit_val);
        
        if (param != null) {
            edit_key.setText(param.getKey());
            edit_val.setText(param.getValue());
        }
        
        builder.setView(view).setPositiveButton("Commit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key = edit_key.getText() == null ? "" : edit_key.getText().toString().trim();
                String val = edit_val.getText() == null ? "" : edit_val.getText().toString().trim();
    
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(getContext(), "Key can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null)
                    listener.onCommit(new UserDefinedData.UserDefinedParam(key, val));
                
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
}
