package top.bingchenglin.easyframework.commons.validator;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.bingchenglin.easyframework.commons.annotation.AttrDateTime;
import top.bingchenglin.easyframework.commons.annotation.AttrDefinition;
import top.bingchenglin.easyframework.commons.annotation.AttrEnum;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: 统一校验公共方法, API说明如下：
 * validate(java.lang.Object)  第一个入参为被校验的对象实例
 * validate(java.lang.Object, java.lang.String...)  第一个入参为被校验的对象实例 , 第二个入参开始是过滤校验的节点
 *
 * 例子：
 * Validator.validate(object, "servNumber");  //出现名称为servNumber的所有节点都会被过滤
 * Validator.validate(object, "createChargeVirGoodsOrderReq.servNumber"); //类createChargeVirGoodsOrderReq的属性servNumber会被过滤
 * Validator.validate(object, "requestHeader.createChargeVirGoodsOrderReq.servNumber"); //类createChargeVirGoodsOrderReq的属性servNumber会被过滤(更精准定位节点)
 * Validator.validate(object, "requestHeader.createChargeVirGoodsOrderReq.servNumber", "attrInfo.attrName", "attrInfo.attrid");  //同时过滤多个节点
 *
 * PS: 校验不通过是以异常返回的, 使用方可以捕捉异常类AttrValidationException并获得校验不通过的节点：filedName和异常码：retcode 和异常说明：message
 */
public class Validator {
    private static final Logger LOGGER = LogManager.getLogger(Validator.class);

    private static final ConcurrentMap<Class, ClassReflect> classReflectedMap = new ConcurrentHashMap<Class, ClassReflect>();

    private static final Map<Class, Class> classMap = new HashMap<Class, Class>();

    static {
        synchronized (classMap) {
            if(classMap.isEmpty()) {

                classMap.put(AttrDefinition.class, AttrDefinitionValidatorImpl.class);
                classMap.put(AttrDateTime.class, AttrDateTimeValidatorImpl.class);
                classMap.put(AttrEnum.class, AttrEnumValidatorImpl.class);
                extension();
            }

        }
    }

    private static void extension() {
        Properties properties = initProperties("validator.properties");
        LOGGER.info("统一校验扩展注解 {}", properties);

        for (Object o : properties.keySet()) {
            Object impl = properties.get(o);
            String key = (String) o;
            String value = (String) impl;
            try {
                Class<?> keyClass = Class.forName(key);
                Class<?> valueClass = Class.forName(value);
                Class<?>[] interfaces = valueClass.getInterfaces();
                if(interfaces != null) {
                    for (Class cls : interfaces) {
                        if("com.asiainfo.gdm.jcoc.common.validator.AttrValidator".equals(cls.getName())) {
                            classMap.put(keyClass, valueClass);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error(e);
            }
        }
    }

    public static void validate(Object obj) throws AttrValidationException {
        validate(obj, null);
    }

    private static void validate(Object obj, String preFileName, String... filters) throws AttrValidationException {

        try {
            Class<?> cls = obj.getClass();

            ClassReflect classReflect = initClassReflect(cls);

//            List<Method> list = new ArrayList<Method>();
//
//            getMethods(cls, list);

//            Field[] declaredFields = obj.getClass().getDeclaredFields();
//            Field[] declaredFields = classReflect.getFields();

            methodsfor:
            for (Method method : classReflect.getMethods()) {

                String fieldName = StringUtils.uncapitalize(method.getName().substring(3));
                String node = preFileName + fieldName;
                if(filters != null) {

                    for (String filter : filters) {
                        if(node.contains(filter)) {
                            continue methodsfor;
                        }
                    }
                }

                if (getFieldByName(fieldName, classReflect.getFields()) == null) {
                    continue;
                }

                Object value = method.invoke(obj);
                for (Annotation annotation : getAnnotations(method, fieldName, classReflect)) {

                    LOGGER.debug(method.getName() + " " + annotation.annotationType().getName());
                    if (classMap.containsKey(annotation.annotationType())) {

                        Class aClass = classMap.get(annotation.annotationType());
                        AttrValidator validator = (AttrValidator) aClass.newInstance();

                        LOGGER.debug("开始校验节点:[{}] 节点值为:[{}], 校验方式：[{}]", node, value, annotation.toString());
                        validator.doProcess(annotation, node, value);
                    }
                }

                if (value != null && !(value instanceof String
                        || value instanceof Integer
                        || value instanceof Float
                        || value instanceof Double
                        || value instanceof Collection
                        || value instanceof Map
                        || value instanceof Date
                        || value instanceof Timestamp)) {
                    validate(value, preFileName + getFieldSimpleName(value), filters);
                }

                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    for (Object o : collection) {
                        if (o != null && !(o instanceof String
                                || o instanceof Integer
                                || o instanceof Float
                                || o instanceof Double
                                || o instanceof Collection
                                || o instanceof Map
                                || o instanceof Date
                                || o instanceof Timestamp)) {
                            validate(o, preFileName + getFieldSimpleName(o), filters);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error(e);
        } catch (InvocationTargetException e) {
            LOGGER.error(e);
        } catch (InstantiationException e) {
            LOGGER.error(e);
        }
    }

    public static ClassReflect initClassReflect(Class cls) {
        if (!classReflectedMap.containsKey(cls)) {
            List<Method> list = new ArrayList<Method>();
            getMethods(cls, list);
            Field[] fields = cls.getDeclaredFields();
            ClassReflect classReflect = new ClassReflect(fields, list.toArray(new Method[]{}));
            classReflectedMap.put(cls, classReflect);
        }
        return classReflectedMap.get(cls);
    }

    @Deprecated
    private static Field getFieldByName(String fieldName, Class cls) {
        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            LOGGER.debug(e);
        }
        return null;
    }

    private static Field getFieldByName(String fieldName, Field[] fields) {
        for (Field f : fields) {
            if(f.getName().equals(fieldName)) {
                return f;
            }
        }
        LOGGER.debug("fileName: {} not found", fieldName);
        return null;
    }

    private static Collection<Annotation> getAnnotations(Method method, Field field) {
        Set<Annotation> sets = new HashSet<Annotation>();
        if(field != null) {
            sets.addAll(Arrays.asList(method.getDeclaredAnnotations()));
            sets.addAll(Arrays.asList(field.getDeclaredAnnotations()));
        }
        return sets;
    }

    private static Collection<Annotation> getAnnotations(Method method, String fieldName, ClassReflect classReflect) {
        ConcurrentMap<String, Collection<Annotation>> annotationMap = classReflect.getAnnotationMap();
        if(!annotationMap.containsKey(fieldName)) {
            Collection<Annotation> annotations = getAnnotations(method, getFieldByName(fieldName, classReflect.getFields()));
            annotationMap.put(fieldName, annotations);
        }
        return annotationMap.get(fieldName);
    }

    public static void validate(Object obj, String... filters) throws AttrValidationException {
        long start = System.currentTimeMillis();
        validate(obj, getFieldSimpleName(obj), filters);
        long end = System.currentTimeMillis();
//        System.out.println("validate cost " + (end - start) + "ms");
        LOGGER.debug("validate cost {}ms", (end - start));
    }

    private static String getFieldSimpleName(Object o) {
        return StringUtils.uncapitalize(o.getClass().getSimpleName()) + ".";
    }

    private static void getMethods(Class cls, List<Method> lists) {
        Class superclass = cls.getSuperclass();
        if (superclass != null) {
            getMethods(superclass, lists);
        }

        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")
                    && method.getName().length() > 3
                    && (method.getReturnType() != Class.class
                    && method.getReturnType() != void.class)
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                lists.add(method);
            }
        }
    }

    private static Properties initProperties(String path) {
        Properties prop = new Properties();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inStream = classLoader.getResourceAsStream(path);
            if (inStream == null) {
                throw new FileNotFoundException(path + " Not Found!");
            }
            prop.load(inStream);
            LOGGER.info("{} load success.", path);
        } catch (Exception e) {}
        return prop;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        Validator validator = new Validator();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            validator.getFieldByName("valueIntegerLength", Validator.class);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");

        start = System.currentTimeMillis();
        Field[] declaredFields = AttrLengthVO.class.getDeclaredFields();
        for (int i = 0; i < 100000; i++) {
            validator.getFieldByName("valueIntegerLength", declaredFields);
        }
        end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
    }
}
