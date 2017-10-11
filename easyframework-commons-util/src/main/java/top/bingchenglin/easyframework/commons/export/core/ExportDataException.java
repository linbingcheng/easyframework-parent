package top.bingchenglin.easyframework.commons.export.core;

import java.io.IOException;


public class ExportDataException extends IOException {

    public ExportDataException() {
    }

    public ExportDataException(String message) {
        super(message);
    }

    public ExportDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportDataException(Throwable cause) {
        super(cause);
    }
}
