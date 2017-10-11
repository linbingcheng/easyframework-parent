package top.bingchenglin.easyframework.commons.realm;

public class AuthorizationException extends Exception {
    private String principals;
    private String permission;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String principals, String permission) {
        this(principals, permission, String.format("用户\"%s\"没有\"%s\"操作权限！", principals, permission));
    }

    public AuthorizationException(String principals, String permission, String message) {
        super(message);
        this.principals = principals;
        this.permission = permission;
    }

    public String getPrincipals() {
        return principals;
    }

    public String getPermission() {
        return permission;
    }
}
