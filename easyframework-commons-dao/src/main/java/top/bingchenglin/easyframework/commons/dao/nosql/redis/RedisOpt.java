package top.bingchenglin.easyframework.easyframwork.commons.dao.nosql.redis;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOpt {
	
	String KeyPrefix() default "";
	long LiveTime() default -1L;
	TimeUnit timeUnit() default TimeUnit.SECONDS;
}
