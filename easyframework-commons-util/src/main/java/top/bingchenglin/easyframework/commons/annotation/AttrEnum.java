package top.bingchenglin.easyframework.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法：
 *
 * @AttrEnum(enumValue = "07,09,69,61,62,63")
 * public String getBizType() {
 *      return bizType;
 * }
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttrEnum {
    String enumValue() default "";

    String retcode() default "";
}