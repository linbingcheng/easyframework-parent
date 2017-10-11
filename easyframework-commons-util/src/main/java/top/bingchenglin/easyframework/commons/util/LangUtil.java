package top.bingchenglin.easyframework.commons.util;

public class LangUtil {
    public static String valueOf(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    public static boolean booleanOf(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj == null) {
            return false;
        }
        String objStr = obj.toString();
        if ("1".equals(objStr) || "true".equals(objStr)) {
            return true;
        }
        return false;
    }

    public static String boolean2String(Boolean value) {
        return value != null && value.booleanValue() ? "1" : "0";
    }
}
