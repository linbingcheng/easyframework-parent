package bingchenglin.top.commons.dao.db.router;

import org.apache.commons.lang3.StringUtils;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RWSeparateRoutingInterceptor {
    public static final String SLAVE = "slave";
    private static final Logger LOGGER = LoggerFactory.getLogger(RWSeparateRoutingInterceptor.class);
    private String slave = SLAVE;
    private String basePkName;

    public String getBasePkName() {
        return basePkName;
    }

    public void setBasePkName(String basePkName) {
        this.basePkName = basePkName;
    }

    public String getSlave() {
        return slave;
    }

    public void setSlave(String slave) {
        this.slave = slave;
    }

    public void before(JoinPoint jp) {
        Object[] args = jp.getArgs();
        String methodName = jp.getSignature().getName();

        if (args.length > 0 && args[0] instanceof DSRoutable) {
            DSRoutable param = (DSRoutable) args[0];
            setDSFlag(param.getDSFlag(), methodName);
        } else {
            String pkName = jp.getTarget().getClass().getPackage().getName();
            pkName = pkName.substring(basePkName.length() + 1);
            String module = pkName.substring(0, pkName.indexOf("."));

            setDSFlag(module, methodName);
        }

        LOGGER.debug("入参ds={}", DSRoutingHolder.getDSFlag());
    }

    public void after(JoinPoint jp) {

        DSRoutingHolder.removeDSFlag();
    }

    private void setDSFlag(String dsFlag, String methodName) {
        DSRoutingHolder.setDSFlag(dsFlag + useSlave(methodName));
    }

    private String useSlave(String methodName) {
        return (StringUtils.isNotBlank(methodName) && !methodName.startsWith("do")) ? getSlave() : "";
    }

}
