package com.excalibur.core.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column
 * Date: 14-2-18
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    public String value();

    /**
     * 当需要把某个成员对象转换为JSON格式的String保存在DB中时，可以设置其开启
     *
     * @return
     */
    public boolean isJsonText() default false;

    /**
     * 当 isJsonText == true 时，
     * 如果Field是一个集合对象，则需要设置collection & element，
     * 以便在从String转换时使用正确的JavaType
     *
     * @return
     */
    public Class<?> collection() default Object.class;

    public Class<?>[] element() default {};

}
