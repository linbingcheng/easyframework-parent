package top.bingchenglin.easyframework.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version v1.0
 * @职责
 * @anthor Jipang Luo
 * @since 2016/7/11
 */
public class EnumUtil {
    /**
     * 根据枚举已知值取符合条件的枚举对象数组
     * @param anEnum 枚举类
     * @param getMethodName 取值方法
     * @param value 已知值
     * @return
     */
    public static <T extends Enum<T>> T[] getEnumsByValue(Class<T> anEnum, String getMethodName, String value){

        T[] ts = null;
        try {
            if(anEnum == null || StringUtils.isEmpty(getMethodName) || StringUtils.isEmpty(value)){
                throw new Exception();
            }
            T[] enumConstants = anEnum.getEnumConstants();
            List<T> list = new ArrayList<T>();
            for(T en : enumConstants){
                Object va = anEnum.getMethod(getMethodName).invoke(en);
                if(va != null && va.toString().equals(value)){
                    list.add(en);
                }
            }
            ts = (T[]) list.toArray(new Object[list.size()]);

        }catch (Exception e){

        }
        return ts;
    }
}
