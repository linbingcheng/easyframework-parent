package top.bingchenglin.easyframework.commons.export.core;

public abstract class ExportDataCreator implements ExportData {

    public final void export() throws ExportDataException {

        try {
            beforeWrite();
            writeTitle();
            writeBody();
            writeElse();
            afterWrite();
        } finally {
            close();
        }
    }

    protected abstract void close() throws ExportDataException;

    protected abstract void writeBody() throws ExportDataException;

    protected abstract void writeTitle() throws ExportDataException;

    protected void writeElse() throws ExportDataException {
    }

    protected void beforeWrite() throws ExportDataException {
    }

    protected void afterWrite() throws ExportDataException {
    }


}
