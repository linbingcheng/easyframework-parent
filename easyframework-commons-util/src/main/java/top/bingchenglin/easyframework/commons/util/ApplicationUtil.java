package top.bingchenglin.easyframework.commons.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ApplicationUtil {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationUtil.class);

    private static final String[] SPRING_CONFIG_LOCATION = {"classpath*:META-INF/applicationContext.xml"};

    private static ApplicationContext APPLICATION_CONTEXT = null;

    private static Lock lock = new ReentrantLock();

    private ApplicationUtil() {
        // ignore
    }

    public static void init() {
        System.setProperty("org.jboss.logging.provider", "slf4j");

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        BeanUtilsBean.getInstance().getConvertUtils().register(false, true, 0);
        DateConverter dateConverter = new DateConverter(null);
        dateConverter.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"});
        ConvertUtils.register(dateConverter, java.util.Date.class);

        LOGGER.info("application init finish.");
    }

    public static ApplicationContext initContext() {
        return initContext(SPRING_CONFIG_LOCATION);
    }

    public static ApplicationContext initContext(String... configLocations) {
        if (APPLICATION_CONTEXT != null) {
            return APPLICATION_CONTEXT;
        }
        lock.lock();
        try {
            if (APPLICATION_CONTEXT == null) {
                ApplicationContext context = new ClassPathXmlApplicationContext(configLocations);
                initContext(context);
            }
        } finally {
            lock.unlock();
        }
        return APPLICATION_CONTEXT;
    }

    public static ApplicationContext initContext(ApplicationContext context) {
        if (context != null) {
            APPLICATION_CONTEXT = context;
        } else {
            initContext();
        }
        return APPLICATION_CONTEXT;
    }

    public static ApplicationContext getContext() {
        return APPLICATION_CONTEXT;
    }

    public static <T> T getBean(Class<T> beanClazz) {
        return getContext().getBean(beanClazz);
    }

    public static Object getBean(String beanName) {
        if (getContext().containsBean(beanName)) {
            return getContext().getBean(beanName);
        }
        return null;
    }

    public static String getRealPath(String path) {
        ApplicationContext context = getContext();
        if (context instanceof WebApplicationContext) {
            path = ((WebApplicationContext) context).getServletContext().getRealPath(path);
        }
        return path;
    }
}
