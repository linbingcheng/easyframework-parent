package top.bingchenglin.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法：
 *
 * @AttrDefinition(occurrences = "1", length = "V20")
 * public String getServNumber() {
 *      return servNumber;
 * }
 *
 * occurrences 取值如下
 * ?    0..1，可选项
 * *    0..n，可以没有，也可以有多项
 * +    1..n，至少有1项，也可以有多项
 * 1    数字1，代表必须且只能填1项
 *
 * length 取值如下：
 * V20 代表最多20
 * F20 代表固定长度20
 * V(5,10) 代表最少长度为5，最大长度为10
 * F(5,10) 代表只能两种长度，要么是5，要么是10
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttrDefinition {
    String occurrences() default "";

    String length() default "";

    String retcode() default "";
}