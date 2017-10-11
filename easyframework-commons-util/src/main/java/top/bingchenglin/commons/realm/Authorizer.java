package top.bingchenglin.commons.realm;

public interface Authorizer {
    boolean isPermitted(String principals, String permission);

    void checkPermission(String principals, String permission) throws AuthorizationException;
}
