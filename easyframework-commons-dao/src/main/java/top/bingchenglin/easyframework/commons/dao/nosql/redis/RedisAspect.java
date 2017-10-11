package top.bingchenglin.easyframework.easyframwork.commons.dao.nosql.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Method;

@Aspect
public class RedisAspect {
	private static final Logger LOGGER = LogManager.getLogger(RedisAspect.class);
	
	@Autowired
	public ValueOperations<String, Object> valueOperations;
	
	@Pointcut("@annotation(top.bingchenglin.easyframework.easyframwork.commons.dao.nosql.redis.RedisOpt)")
	public void serviceAspect() {
		
	}

	@Around("serviceAspect()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		RedisOpt redisOpt = getRedisOpt(joinPoint);
		
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(redisOpt.KeyPrefix()).append("_");
		keyBuffer.append(targetName).append(".");
		keyBuffer.append(methodName);
		keyBuffer.append("(");
		for (int i = 0; i < arguments.length; i++) {
			Object object = arguments[i];
			keyBuffer.append(arguments[i].getClass().getSimpleName()).append("=").append(object).append(";");
		}
		keyBuffer.append(")");
		
		String key = keyBuffer.toString();
		Object result = valueOperations.get(key);
		if (null == result) {
			result = joinPoint.proceed();
			valueOperations.set(key, result, redisOpt.LiveTime(), redisOpt.timeUnit());
		} else {
			LOGGER.info("击中缓存key=" + key);
		}
		return result;
	}

	@AfterThrowing(pointcut = "serviceAspect()", throwing = "ex")
	public void afterThrowing(Exception ex) {
		LOGGER.error(ex);
	}
	
	private static RedisOpt getRedisOpt(JoinPoint joinPoint) throws ClassNotFoundException{
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class<?> targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		RedisOpt redisOpt = null;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class<?>[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					redisOpt = method.getAnnotation(RedisOpt.class);
					break;
				}
			}
		}
		return redisOpt;
	}
}
