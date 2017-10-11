package top.bingchenglin.commons.export.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.bingchenglin.commons.export.core.CommonExportData;
import top.bingchenglin.commons.export.core.Constant;
import top.bingchenglin.commons.export.core.ExportDataException;
import top.bingchenglin.commons.export.core.PropertyFormat;
import top.bingchenglin.commons.export.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.ecop.export.impl.ExportTxtDataBean
 * @Description: ExportTxtDataBean
 * @date 2016/6/13 17:35
 */
public class ExportXlsData extends CommonExportData {
    private String EXCEL_HEAD = "<head>\r\n"
            + "   <meta http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"
            + "   <meta name=ProgId content=Excel.Sheet>\r\n"
            + "   <meta name=Generator content=\"Microsoft Excel 11\">\r\n"
            + "</head>\r\n" + "<style>\r\n" + "td {\r\n"
            + "   border:#a8a8a8 soild 1px;\r\n" + "   color:#000;\r\n"
            + "   font-size:12px;\r\n" + "   text-align:center;\r\n"
            + "   background:#FFFFFF;\r\n"
            + "   mso-number-format:\"\\@\";\r\n" + "}\r\n" + "</style>\r\n";

    private boolean hasindex;
    private int rowCount = 0;
    private String headtitle;
    private String[] subHeadTitle = null;

    public ExportXlsData(Object obj, String properties, Object param) {
        super(obj, properties, param);
    }

    public ExportXlsData(Object obj, String properties, String fileName, Object param) {
        super(obj, properties, fileName, param);
    }

    public void setHeadtitle(String headtitle) {
        this.headtitle = headtitle;
    }

    public void setSubHeadTitle(String[] subHeadTitle) {
        this.subHeadTitle = subHeadTitle;
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
            getOutputStream().write(EXCEL_HEAD.getBytes());
            writeHeadTitle();
            writeSubHeadTitle();
            os.write(new StringBuffer("<table border=1 bordercolor=#A8A8A8>").append(Constant.KEYWORD_ENTER).toString().getBytes());
            if (CollectionUtils.isNotEmpty(getTitles())) {
                if (isHasindex()) {
                    os.write(Utils.cvtToGBK("<tr><td>序号</td>"));
                    os.write(new String(Constant.KEYWORD_ENTER).getBytes());
                }


                for (int i = 0; i < getTitles().size(); i++) {
                    if (i == (getTitles().size() - 1)) {
                        os.write(Utils.cvtToGBK("<td>" + getTitles().get(i) + "</td></tr>"));
                        os.write(new String(Constant.KEYWORD_ENTER).getBytes());
                    } else {
                        os.write(Utils.cvtToGBK("<td>" + getTitles().get(i) + "</td>"));
                    }
                }
            }
        } catch (IOException e) {
            throw new ExportDataException(e);
        }
    }

    private void writeHeadTitle() throws ExportDataException {
        try {
            if (StringUtils.isNotBlank(headtitle)) {
                StringBuffer sb = new StringBuffer();
                sb.append("<table bordercolor=#A8A8A8><tr>")
                        .append("<td colspan=").append(getTitles().size() + 1).append(">")
                        .append("<b>")
                        .append(headtitle)
                        .append("</b>")
                        .append("</td></tr>");
                getOutputStream().write(Utils.cvtToGBK(sb.toString()));
                sb.setLength(0);
                sb.append("</table>");
            }
        } catch (IOException e) {
            throw new ExportDataException(e);
        }
    }

    private void writeSubHeadTitle() throws ExportDataException {
        try {
            if (subHeadTitle != null && subHeadTitle.length > 0 && CollectionUtils.isNotEmpty(getTitles())) {
                StringBuffer sb = new StringBuffer();
                sb.append("<table bordercolor=#A8A8A8><tr>");
                for (int i = 0; i < subHeadTitle.length; i++) {
                    if (subHeadTitle[i] != null && subHeadTitle[i].equals(",")) {
                        sb.append("</tr><tr>");
                        continue;
                    }
                    if (hasindex) {
                        sb.append("<td>").append(subHeadTitle[i]).append("</td>");
                    } else {
                        sb.append("<td  colspan=5>").append(subHeadTitle[i]).append("</td>");
                    }

                    if (i == subHeadTitle.length - 1)
                        sb.append("</tr>");
                }
                sb.append("</table>");
                getOutputStream().write(Utils.cvtToGBK(sb.toString()));
            }
        } catch (IOException e) {
            throw new ExportDataException(e);
        }
    }

    @Override
    protected void write(List list) throws ExportDataException {
        try {

            //print datas

            if (CollectionUtils.isNotEmpty(list)) {

                for (Object ob : list) {
                    StringBuffer buff = new StringBuffer();
                    buff.append("<tr>");
                    rowCount++;

                    if (CollectionUtils.isEmpty(getPropertyList())) {
                        break;
                    }

                    if (isHasindex()) {
                        buff.append("<td>").append(rowCount).append("</td>");
                    }

                    for (int i = 0; i < getPropertyList().size(); i++) {
                        PropertyFormat propertyFormat = getPropertyList().get(i);

                        if (Constant.EXPRESSION.equals(propertyFormat.getFormat())) { //处理两值相加的表达式
                            String value = Utils.expression(ob, propertyFormat.getPropertyName(), getPropertyList());
                            buff.append("<td>").append(value).append("</td>");

                        } else {
                            Object value = Utils.getValue(ob, propertyFormat.getPropertyName());
                            buff.append("<td>").append(Utils.formatData(propertyFormat, value))
                                    .append("</td>");
                        }
                    } // end for
                    buff.append("</tr>");
                    buff.append(Constant.KEYWORD_ENTER);
                    getOutputStream().write(Utils.cvtToGBK(buff.toString()));
                    flush();
                } // end while
            }
        } catch (Exception e) {
            throw new ExportDataException(e);
        } finally {
            flush();
        }
    }

}
