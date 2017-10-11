package top.bingchenglin.easyframework.commons.validator;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.validator.AttrValidationException
 * @Description: AttrValidationException
 * @date 2016/5/25 16:35
 */
public class AttrValidationException extends IllegalArgumentException {

    private String filedName;
    private String retcode;

    public AttrValidationException(String filedName, String retcode, String s) {
        super(s);
        this.filedName = filedName;
        this.retcode = retcode;
    }
    public AttrValidationException(String filedName, String retcode, String s, Throwable cause) {
        super(s, cause);
        this.filedName = filedName;
        this.retcode = retcode;
    }

    public AttrValidationException() {
        super();
    }

    public AttrValidationException(String s) {
        super(s);
    }

    public AttrValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttrValidationException(Throwable cause) {
        super(cause);
    }

    public String getRetcode() {
        return retcode;
    }

    public AttrValidationException setRetcode(String retcode) {
        this.retcode = retcode;
        return this;
    }

    public String getFiledName() {
        return filedName;
    }

    public AttrValidationException setFiledName(String filedName) {
        this.filedName = filedName;
        return this;
    }
}
