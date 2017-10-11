package top.bingchenglin.easyframework.commons.export.core;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public abstract class CommonExportData extends ExportDataCreator {

    private OutputStream outputStream;
    private boolean pageQueryFlag = false;
    private int startIndex;
    private int endIndex;
    private String queryMethodName = "export";
    private Object obj;
    private Object param;
    private String properties;
    private String fileName = "export";
    private List<String> titles = new ArrayList<String>();
    private List<PropertyFormat> propertyList;

    public CommonExportData(Object obj, OutputStream outputStream, String properties, String fileName, Object param) {
        this.obj = obj;
        this.properties = properties;
        this.fileName = fileName;
        this.outputStream = outputStream;
        this.param = param;
    }

    public CommonExportData(Object obj, String properties, String fileName, Object param) {
        this.obj = obj;
        this.properties = properties;
        this.fileName = fileName;
        this.param = param;
    }

    public CommonExportData(Object obj, String properties, Object param) {
        this.obj = obj;
        this.properties = properties;
        this.param = param;
    }

    public void setPageQueryFlag(boolean pageQueryFlag) {
        this.pageQueryFlag = pageQueryFlag;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setQueryMethodName(String queryMethodName) {
        this.queryMethodName = queryMethodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<PropertyFormat> getPropertyList() {
        return propertyList;
    }


    public List<String> getTitles() {
        return titles;
    }
//
//    @Override
//    protected void writeBody() throws ExportDataException {
//        try {
//            DBQueryParam params;
//            if (param != null) {
//                params = (DBQueryParam) param;
//            } else {
//                params = new DBQueryParam();
//            }
//            List list;
//            if ("0".equals(params.getLimit())) {//因为导出都是所有的导出，但为效率，采用分页的方式导出数据
//                params.setLimit(Constant.EXCELOUT_PAGE_SIZE);//设置每面最多的记录数
//                params.setQueryAll(false);
//            }
//            for (int startindex = 1; ; startindex++) {
//                if (pageQueryFlag) {
//                    if (startindex < startIndex) continue;
//                    if (startindex > endIndex) break;
//                }
//                params.setPage(startindex);//设置导出的页码
//                Object result = MethodUtils.invokeMethod(obj, queryMethodName, params);
//                DataPackage dp = (DataPackage) result;
//                if (dp != null && CollectionUtils.isNotEmpty(dp.getData())) {
//                    list = dp.getData();
//                    write(list);
//                    int pagesize = params.getLimit();
//                    if (list.size() < pagesize || ((dp.getTotal() + pagesize - 1) / pagesize) <= startindex) {// 代表最后一页
//                        break;
//                    }
//                } else {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            throw new ExportDataException(e);
//        }
//    }

    protected abstract void write(List list) throws ExportDataException;

    protected OutputStream getOutputStream() throws ExportDataException {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    protected void beforeWrite() throws ExportDataException {
        super.beforeWrite();

        init();

    }


    protected void flush() throws ExportDataException {
        try {
            getOutputStream().flush();
        } catch (IOException e) {
            throw new ExportDataException(e);
        }
    }

    private void init() throws ExportDataException {
        String[] propertiesArray = properties.split("\\|");
        for (String property : propertiesArray) {
            String[] propertyParam = property.split(";");
            if (NumberUtils.isNumber(propertyParam[0])) {
                // propertyId1;propertyName1;titleCol1|propertyId2;propertyName2;titleCol2;format2;formatType2|......
                if (propertyParam.length == 3) {
                    addOutputProperty(Integer
                                    .parseInt(propertyParam[0]), propertyParam[1],
                            propertyParam[2], null, null);
                } else if (propertyParam.length == 5) {
                    addOutputProperty(Integer
                                    .parseInt(propertyParam[0]), propertyParam[1],
                            propertyParam[2], propertyParam[3],
                            propertyParam[4]);
                } else {
                    throw new ExportDataException(property + "格式不正确");
                }
            } else {
                // propertyName1;titleCol1;|propertyName2;titleCol2;format2;formatType2|......
                if (propertyParam.length == 2) {
                    addOutputProperty(
                            propertyParam[0], propertyParam[1]);
                } else if (propertyParam.length == 4) {
                    addOutputProperty(
                            propertyParam[0], propertyParam[1], propertyParam[2],
                            propertyParam[3]);
                } else {
                    throw new ExportDataException(property + "格式不正确");
                }
            }
        }
    }

    private void addOutputProperty(int propertyId, String propertyName,
                                   String titleCol, String format, String formatType) {
        addOutputProperty(propertyId, propertyName, format, formatType);
        titles.add(titleCol);
    }

    private void addOutputProperty(int propertyId, String propertyName,
                                   String format, String formatType) {
        if (propertyList == null) {
            propertyList = new ArrayList();
        }
        propertyList.add(new PropertyFormat(propertyId, propertyName, format, formatType));
    }

    private void addOutputProperty(String propertyName, String titleCol) {
        addOutputProperty(0, propertyName, titleCol, null, null);
    }

    private void addOutputProperty(String propertyName, String titleCol,
                                   String format, String formatType) {
        addOutputProperty(0, propertyName, titleCol, format, formatType);
    }

    protected void close() throws ExportDataException {
        if (getOutputStream() != null) {
            try {
                getOutputStream().flush();
                getOutputStream().close();
            } catch (IOException e) {
                throw new ExportDataException(e);
            }
        }
    }

    public void setPage(boolean flag, int startIndex, int endIndex) {
        setPageQueryFlag(flag);
        setStartIndex(startIndex);
        setEndIndex(endIndex);
    }
}
