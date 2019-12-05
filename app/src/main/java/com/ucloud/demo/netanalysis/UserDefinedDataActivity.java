package com.ucloud.demo.netanalysis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ucloud.library.netanalysis.exception.UCParamVerifyException;
import com.ucloud.library.netanalysis.module.UserDefinedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by joshua on 2019-08-03 13:10.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UserDefinedDataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public final String TAG = getClass().getSimpleName();
    
    public static final int REQUEST_CODE = 1000;
    
    private ListView listview_user_defined_data;
    private LinearLayout layout_opt_btn, layout_delete_btn;
    private UserDefinedDataAdapter adapter;
    private SharedPreferences mSharedPreferences;
    private UserDefinedData data;
    private Map<String, String> map;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_defined_data);
        
        setTitle("Edit User Defined Data");
        
        List<UserDefinedDataAdapter.AdapterData> list;
        map = new ArrayMap<>();
        
        listview_user_defined_data = findViewById(R.id.listview_user_defined_data);
        listview_user_defined_data.setOnItemClickListener(this);
        layout_opt_btn = findViewById(R.id.layout_opt_btn);
        layout_delete_btn = findViewById(R.id.layout_delete_btn);
        findViewById(R.id.txt_submit).setOnClickListener(this);
        findViewById(R.id.txt_add).setOnClickListener(this);
        findViewById(R.id.txt_select_all).setOnClickListener(this);
        findViewById(R.id.txt_delete).setOnClickListener(this);
        
        mSharedPreferences = ((DemoApplication) getApplication()).getSharedPreferences();
        
        String dataJson = mSharedPreferences.getString("user_defined_data", null);
        if (!TextUtils.isEmpty(dataJson)) {
            list = prepareData(dataJson);
        } else {
            list = new ArrayList<>();
        }
        adapter = new UserDefinedDataAdapter(this, list);
        listview_user_defined_data.setAdapter(adapter);
    }
    
    private List<UserDefinedDataAdapter.AdapterData> prepareData(String dataJson) {
        List<UserDefinedDataAdapter.AdapterData> list = new ArrayList<>();
        try {
            JSONObject jobj = new JSONObject(dataJson);
            Iterator<String> iterator = jobj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String val = jobj.optString(key, null);
                map.put(key, val);
                list.add(new UserDefinedDataAdapter.AdapterData(
                        new UserDefinedData.UserDefinedParam(key, val)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_submit: {
                JSONObject jobj = new JSONObject();
                
                Set<String> set = map.keySet();
                for (String key : set) {
                    try {
                        jobj.put(key, map.get(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                
                try {
                    data = new UserDefinedData.Builder(map).create();
                    mSharedPreferences.edit().putString("user_defined_data", jobj.toString()).apply();
                    Intent result = new Intent();
                    Bundle bd = new Bundle();
                    result.putExtra("user_defined_data", jobj.toString());
                    setResult(RESULT_OK, result);
                    finish();
                } catch (UCParamVerifyException e) {
                    new AlertDialog.Builder(UserDefinedDataActivity.this)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).show();
                }
                
                break;
            }
            case R.id.txt_add: {
                new EditDataDialog(new EditDataDialog.OnEditDataListener() {
                    @Override
                    public void onCommit(UserDefinedData.UserDefinedParam param) {
                        if (map.containsKey(param.getKey())) {
                            Toast.makeText(UserDefinedDataActivity.this, String.format("[key]:%s is exist", param.getKey()),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        map.put(param.getKey(), param.getValue());
                        adapter.addData(new UserDefinedDataAdapter.AdapterData(param));
                    }
                }).show(getSupportFragmentManager(), "Edit Data Dialog");
                break;
            }
            case R.id.txt_select_all: {
                isSelectAll = !isSelectAll;
                adapter.selectAll(isSelectAll);
                break;
            }
            case R.id.txt_delete: {
                List<UserDefinedDataAdapter.AdapterData> list = adapter.delete();
                for (UserDefinedDataAdapter.AdapterData tmp : list) {
                    map.remove(tmp.getParam().getKey());
                }
                setMode(false);
                break;
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        menu_select = menu.findItem(R.id.menu_select);
        return true;
    }
    
    private MenuItem menu_select;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_select) {
            setMode(!isSelectMode);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private boolean isSelectMode = false;
    private boolean isSelectAll = false;
    
    private void setMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
        layout_delete_btn.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);
        layout_opt_btn.setVisibility(isSelectMode ? View.GONE : View.VISIBLE);
        menu_select.setTitle(isSelectMode ? "Done" : "Select");
        adapter.setMode(isSelectMode);
    }
    
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final UserDefinedDataAdapter.AdapterData tmp = adapter.getItem(position);
        new EditDataDialog(new EditDataDialog.OnEditDataListener() {
            @Override
            public void onCommit(UserDefinedData.UserDefinedParam param) {
                if (!TextUtils.equals(tmp.getParam().getKey(), param.getKey()) && map.containsKey(param.getKey())) {
                    Toast.makeText(UserDefinedDataActivity.this, String.format("[key]:%s is exist", param.getKey()),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put(param.getKey(), param.getValue());
                tmp.setParam(param);
                adapter.notifyDataSetChanged();
            }
        }, tmp.getParam()).show(getSupportFragmentManager(), "Edit Data Dialog");
    }
    
    public static Intent startAction(Context context) {
        return new Intent(context, UserDefinedDataActivity.class);
    }
}
