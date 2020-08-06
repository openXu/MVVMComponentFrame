package com.openxu.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标示实体中主键信息
 * 
 * @author Administrator
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ID {
	/**
	 * 是否是自增的
	 * 
	 * @return
	 */
	boolean autoincrement();

}
