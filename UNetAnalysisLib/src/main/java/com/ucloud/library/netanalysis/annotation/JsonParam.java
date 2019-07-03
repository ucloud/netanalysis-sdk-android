package com.ucloud.library.netanalysis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by joshua on 2019/7/3 23:45.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonParam {
    /**
     * @return the desired name of the field when it is serialized or deserialized
     */
    String value();

    /**
     * @return the alternative names of the field when it is deserialized
     */
    String[] alternate() default {};
}
