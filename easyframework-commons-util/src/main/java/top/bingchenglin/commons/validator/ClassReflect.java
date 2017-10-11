package top.bingchenglin.commons.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.validator.ClassReflect
 * @Description: ClassReflect
 * @date 2016/11/15 11:25
 */
public class ClassReflect {
    private Field[] fields;
    private Method[] methods;
    private ConcurrentMap<String, Collection<Annotation>> annotationMap = new ConcurrentHashMap<String, Collection<Annotation>>();

    public ClassReflect(Field[] fields, Method[] methods) {
        this.fields = fields;
        this.methods = methods;
    }

    public ConcurrentMap<String, Collection<Annotation>> getAnnotationMap() {
        return annotationMap;
    }

    public void setAnnotationMap(ConcurrentMap<String, Collection<Annotation>> annotationMap) {
        this.annotationMap = annotationMap;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }
}
