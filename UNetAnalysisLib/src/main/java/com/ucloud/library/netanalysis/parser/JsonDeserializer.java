package com.ucloud.library.netanalysis.parser;


import org.json.JSONException;

/**
 * Created by joshua on 2019/7/3 23:15.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface JsonDeserializer<T> {
    T fromJson(String json) throws JSONException;
}
