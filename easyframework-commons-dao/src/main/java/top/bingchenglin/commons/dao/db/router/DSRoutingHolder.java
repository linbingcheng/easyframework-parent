package bingchenglin.top.commons.dao.db.router;

public class DSRoutingHolder {
    private static final ThreadLocal<String> DS_ROUTING_HOLDER = new ThreadLocal<String>();

    public static void setDSFlag(String dsFlag) {
        DS_ROUTING_HOLDER.set(dsFlag);
    }

    public static String getDSFlag() {
        return DS_ROUTING_HOLDER.get();
    }

    public static void removeDSFlag() {
        DS_ROUTING_HOLDER.remove();
    }
}
