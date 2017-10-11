package top.bingchenglin.commons.export.impl;


import org.apache.commons.collections.CollectionUtils;
import top.bingchenglin.commons.export.core.CommonExportData;
import top.bingchenglin.commons.export.core.Constant;
import top.bingchenglin.commons.export.core.ExportDataException;
import top.bingchenglin.commons.export.core.PropertyFormat;
import top.bingchenglin.commons.export.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class ExportTxtData extends CommonExportData {

    private String separator = Constant.FIELD_SEPARATOR_REGEX;
    private boolean hasindex;
    private int rowCount = 0;

    public ExportTxtData(Object obj, String properties, Object param) {
        super(obj, properties, param);
    }

    public ExportTxtData(Object obj, String properties, String fileName, Object param) {
        super(obj, properties, fileName, param);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isHasindex() {
        return hasindex;
    }

    public void setHasindex(boolean hasindex) {
        this.hasindex = hasindex;
    }

    @Override
    protected void writeBody() throws ExportDataException {

    }

    @Override
    public void writeTitle() throws ExportDataException {

        try {
            OutputStream os = getOutputStream();
            if (CollectionUtils.isNotEmpty(getTitles())) {
                if (isHasindex()) {
                    os.write(Utils.cvtToGBK("序号"));
                    os.write(getSeparator().getBytes());
                }

                for (int i = 0; i < getTitles().size(); i++) {
                    if (i == (getTitles().size() - 1)) {
                        os.write(Utils.cvtToGBK(getTitles().get(i)));
                        os.write(new String(Constant.KEYWORD_ENTER).getBytes());
                    } else {
                        os.write(Utils.cvtToGBK(getTitles().get(i)));
                        os.write(getSeparator().getBytes());
                    }
                }
            }
        } catch (IOException e) {
            throw new ExportDataException(e);
        }
    }

    @Override
    protected void write(List list) throws ExportDataException {

        try {
            if (CollectionUtils.isNotEmpty(list)) {

                for (Object ob : list) {
                    rowCount++;
                    StringBuffer buff = new StringBuffer();

                    if (CollectionUtils.isEmpty(getPropertyList())) {
                        break;
                    }
                    if (isHasindex()) {
                        buff.append(rowCount).append(getSeparator());
                    }
                    for (int i = 0; i < getPropertyList().size(); i++) {
                        PropertyFormat propertyFormat = getPropertyList().get(i);

                        if (Constant.EXPRESSION.equals(propertyFormat.getFormat())) { //处理两值相加的表达式
                            String value = Utils.expression(ob, propertyFormat.getPropertyName(), getPropertyList());
                            buff.append(value).append(getSeparator());

                        } else {
                            Object value = Utils.getValue(ob, propertyFormat.getPropertyName());
                            buff.append(Utils.formatData(propertyFormat, value)).append(getSeparator());
                        }
                    } // end for
                    buff.append(Constant.KEYWORD_ENTER);
                    getOutputStream().write(Utils.cvtToGBK(buff.toString()));
                    flush();
                }
            }
        } catch (Exception e) {
            throw new ExportDataException(e);
        } finally {
            flush();
        }
    }

}
