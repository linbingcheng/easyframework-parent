package top.bingchenglin.easyframework.commons.ftp;

import java.io.IOException;

public class FtpIOException extends IOException {
    public FtpIOException(String message) {
        super(message);
    }

    public FtpIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
