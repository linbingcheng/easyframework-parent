package top.bingchenglin.easyframework.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法：
 * @AttrDateTime(format = "yyyy-MM-dd HH:mm:ss")
 * public String getDate() {
 *      return date;
 * }
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttrDateTime {
    String format() default "";

    String retcode() default "";
}