package com.ucloud.library.netanalysis.parser;

import com.ucloud.library.netanalysis.annotation.JsonParam;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonParser {

    public JSONObject toJson(JsonSerializable object) {
        JSONObject json = new JSONObject();
        if (object == null)
            return json;

        Class<?> raw = object.getClass();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {

                }
            }

            Method[] methods = raw.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {

                }
            }
        }

        return json;
    }

    private List<String> getFieldValue(Field f) {
        JsonParam annotation = f.getAnnotation(JsonParam.class);
        List<String> fieldNames = new ArrayList<>();
        if (annotation != null) {
//            f.setAccessible(true);
            String serializedName = annotation.value();
            serializedName = serializedName == null ? "" : serializedName;
            String[] alternates = annotation.alternate();
            if (alternates == null || alternates.length == 0) {
                fieldNames.add(serializedName);
            } else {
                fieldNames.add(serializedName);
                for (String alternate : alternates) {
                    fieldNames.add(alternate);
                }
            }
        }

        return fieldNames;
    }


    private List<String> getMethodValue(Method m) {
        JsonParam annotation = m.getAnnotation(JsonParam.class);
        List<String> fieldNames = new ArrayList<>();
        if (annotation != null) {
//            m.setAccessible(true);
            String serializedName = annotation.value();
            String[] alternates = annotation.alternate();
            if (alternates.length == 0) {
                fieldNames.add(serializedName);
            } else {
                fieldNames.add(serializedName);
                for (String alternate : alternates) {
                    fieldNames.add(alternate);
                }
            }
        }

        return fieldNames;
    }
}
