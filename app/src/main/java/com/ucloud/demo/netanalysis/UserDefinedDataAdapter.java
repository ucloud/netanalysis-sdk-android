package com.ucloud.demo.netanalysis;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ucloud.library.netanalysis.module.UserDefinedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2019-08-03 15:59.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UserDefinedDataAdapter extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();
    
    private List<AdapterData> datas;
    private LayoutInflater inflater;
    
    public static class AdapterData {
        private UserDefinedData.UserDefinedParam param;
        private boolean isChecked = false;
        
        public AdapterData(UserDefinedData.UserDefinedParam param) {
            this.param = param;
        }
        
        public UserDefinedData.UserDefinedParam getParam() {
            return param;
        }
        
        public void setParam(UserDefinedData.UserDefinedParam param) {
            this.param = param;
        }
        
        public boolean isChecked() {
            return isChecked;
        }
        
        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
    
    public UserDefinedDataAdapter(Context context, List<AdapterData> datas) {
        this.datas = datas == null ? new ArrayList<AdapterData>() : datas;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }
    
    @Override
    public AdapterData getItem(int position) {
        return datas == null ? null : datas.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user_defined_data, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if (datas == null)
            return convertView;
        
        final AdapterData data = datas.get(position);
        if (data == null)
            return convertView;
        
        holder.checkbox_data_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setChecked(isChecked);
            }
        });
        holder.checkbox_data_selected.setChecked(data.isChecked);
        holder.checkbox_data_selected.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);
        holder.txt_key.setText(data.getParam().getKey());
        holder.txt_val.setText(data.getParam().getValue());
        
        return convertView;
    }
    
    private class ViewHolder {
        private AppCompatCheckBox checkbox_data_selected;
        private TextView txt_key, txt_val;
        
        public ViewHolder(View convertView) {
            checkbox_data_selected = convertView.findViewById(R.id.checkbox_data_selected);
            txt_key = convertView.findViewById(R.id.txt_key);
            txt_val = convertView.findViewById(R.id.txt_val);
        }
    }
    
    private boolean isSelectMode = false;
    
    public void setMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
        if (!isSelectMode) {
            for (AdapterData data : datas) {
                data.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }
    
    public void selectAll(boolean isSelectAll) {
        for (AdapterData data : datas) {
            data.setChecked(isSelectAll);
        }
        notifyDataSetChanged();
    }
    
    public List<AdapterData> delete() {
        List<AdapterData> cache = new ArrayList<>();
        for (AdapterData data : datas) {
            if (data.isChecked)
                cache.add(data);
        }
        if (!cache.isEmpty()) {
            datas.removeAll(cache);
            notifyDataSetChanged();
        }
        return cache;
    }
    
    
    public void addData(AdapterData data) {
        if (data == null)
            return;
        
        datas.add(data);
        notifyDataSetChanged();
    }
    
    public List<AdapterData> getDatas() {
        return datas;
    }
}
