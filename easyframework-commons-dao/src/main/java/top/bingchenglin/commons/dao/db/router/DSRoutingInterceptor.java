package bingchenglin.top.commons.dao.db.router;


import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSRoutingInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSRoutingInterceptor.class);

    public void before(JoinPoint jp) {
        Object[] args = jp.getArgs();
        if (args.length <= 0) {
            return;
        }
        Object value = args[0];
        if (!(value instanceof DSRoutable)) {
            return;
        }

        DSRoutable param = (DSRoutable) value;
        LOGGER.debug("入参ds={}", param.getDSFlag());
        DSRoutingHolder.setDSFlag(param.getDSFlag());
    }

    public void after(JoinPoint jp) {
        Object[] args = jp.getArgs();
        if (args.length <= 0) {
            return;
        }
        Object value = args[0];
        if (!(value instanceof DSRoutable)) {
            return;
        }

        DSRoutingHolder.removeDSFlag();
    }
}
