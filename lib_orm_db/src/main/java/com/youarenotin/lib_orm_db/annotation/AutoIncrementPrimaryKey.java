package com.youarenotin.lib_orm_db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lubo on 2016/6/9.
 * lubo_wen@126.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIncrementPrimaryKey {
    public String column() default "primary_key";
}
