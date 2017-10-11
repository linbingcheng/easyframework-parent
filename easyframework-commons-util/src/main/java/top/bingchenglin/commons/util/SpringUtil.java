package top.bingchenglin.commons.util;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderSupport;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @version v1.0
 * @职责
 * @anthor Jipang Luo
 * @since 2016/12/11
 */
public class SpringUtil implements ApplicationContextAware, InitializingBean {
    private final static Logger LOG = Logger.getLogger(SpringUtil.class);

    private static ApplicationContext applicationContext;
    private static Properties properties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContext != null) {
            LOG.info("clean local properties and loading spring properties!");
            //清空
            properties = new Properties();
            try {
                String[] beanNames = applicationContext.getBeanNamesForType(BeanFactoryPostProcessor.class);
                for (String beanName : beanNames) {
                    BeanFactoryPostProcessor beanFactoryPostProcessor = applicationContext
                            .getBean(beanName, BeanFactoryPostProcessor.class);
                    if (beanFactoryPostProcessor instanceof PropertyResourceConfigurer) {
                        PropertyResourceConfigurer propertyResourceConfigurer = (PropertyResourceConfigurer) beanFactoryPostProcessor;

                        Class<PropertiesLoaderSupport> clazz = PropertiesLoaderSupport.class;
                        Method mergeProperties = clazz.getDeclaredMethod("mergeProperties");
                        mergeProperties.setAccessible(true);
                        Properties tmpProp = (Properties) mergeProperties.invoke(propertyResourceConfigurer);

                        Method convertProperties = PropertyResourceConfigurer.class.
                                getDeclaredMethod("convertProperties", Properties.class);

                        convertProperties.setAccessible(true);
                        convertProperties.invoke(propertyResourceConfigurer, tmpProp);

                        properties.putAll(tmpProp);
                    }

                }

            } catch (Exception e) {
                LOG.error("spring properties loading error!", e);
            }
        }
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            LOG.info("applicationContext为空，请确保SpringUtil在spring中配置并已经完成初始化");
        }
        return applicationContext;
    }

    public static Properties getProperties() {
        if (properties == null) {
            LOG.info("properties为空，请确保SpringUtil在spring中配置并已经完成初始化");
        }
        return properties;
    }

    //    public void setProperties(Properties properties) {
    //        this.properties = properties;
    //    }

    public static <T> T getBean(Class<T> beanClazz) {
        if (getApplicationContext() == null) {
            return null;
        }
        return applicationContext.getBean(beanClazz);
    }

    public static Object getBean(String beanName) {
        if (getApplicationContext() == null) {
            return null;
        }
        return applicationContext.getBean(beanName);
    }
}
