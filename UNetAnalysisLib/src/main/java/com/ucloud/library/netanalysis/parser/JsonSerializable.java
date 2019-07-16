package com.ucloud.library.netanalysis.parser;


import org.json.JSONObject;

/**
 * Created by joshua on 2019/7/3 23:15.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface JsonSerializable {
    JSONObject toJson();
}
