package top.bingchenglin.commons.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EntityUtil {
    private static final Logger LOGGER = LogManager.getLogger(EntityUtil.class);

    private EntityUtil() {
        // ignore
    }

    public static void copyProperties(Object dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            LOGGER.error("BeanUtils copyProperties failure!", e);
        }
    }

    public static <T> T copyProperties(Object orig, Class<T> cl) {
        T t = null;
        try {
            if (orig != null) {
                t = cl.newInstance();
                EntityUtil.copyProperties(t, orig);
            }
        } catch (Exception e) {
            LOGGER.error("BeanUtils copyProperties failure!", e);
        }
        return t;
    }
}
