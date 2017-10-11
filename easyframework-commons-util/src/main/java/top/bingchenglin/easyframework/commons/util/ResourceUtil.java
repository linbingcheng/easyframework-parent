package top.bingchenglin.easyframework.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ResourceUtil {
    private ResourceUtil() {
        // ignore
    }

    public static String getString(String key, Class cls) {
        Package pkg = cls.getPackage();
        String packagePath = pkg != null ? pkg.getName() : "";
        String resourceName = cls.getSimpleName();
        return getString(key, packagePath, resourceName);
    }

    public static String getString(String key, Package pkg, String resourceName) {
        String packagePath = pkg != null ? pkg.getName() : "";
        return getString(key, packagePath, resourceName);
    }

    public static String getString(String key, String packagePath, String resourceName) {
        ResourceBundle bundle = getBundle(packagePath, resourceName);
        return bundle != null && bundle.containsKey(key) ? bundle.getString(key) : StringUtils.EMPTY;
    }

    private static ResourceBundle getBundle(String packagePath, String resourceName) {
        ResourceBundle bundle = null;
        String[] pkgs = packagePath.split("\\.");
        for (int i = pkgs.length; i >= 0; --i) {
            StringBuilder baseNameBuilder = new StringBuilder();
            baseNameBuilder.append(StringUtils.join(pkgs, ".", 0, i));
            if (baseNameBuilder.length() >= 1) {
                baseNameBuilder.append(".");
            }
            baseNameBuilder.append(resourceName);
            try {
                bundle = ResourceBundle.getBundle(baseNameBuilder.toString());
                break;
            } catch (MissingResourceException ex) {
                // ignore
            }
        }
        return bundle;
    }
}
