package top.bingchenglin.commons.export.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.bingchenglin.commons.export.c2n.Translatable;
import top.bingchenglin.commons.export.core.ExportDataException;
import top.bingchenglin.commons.export.core.PropertyFormat;
import top.bingchenglin.commons.util.ApplicationUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    private static Translatable translatable = null;

    public static byte[] cvtToGBK(String str) {
        if (str == null) {
            return "".getBytes();
        } else {
            try {
                return str.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                return str.getBytes();
            }
        }
    }

    public static boolean isNumeric(String src, boolean isSign) {
        if (isSign)
            return src.matches("([+-]?)([0-9]+)(\\.?)([0-9]*)");
        else
            return src.matches("([0-9]+)(\\.?)([0-9]*)");
    }

    public static String expression(Object property, String expression, List<PropertyFormat> propertyList) throws ExportDataException {
        int pos = 0;
        List propertys = new ArrayList();
        List oprs = new ArrayList(0);
        splitOpr(expression, propertys, oprs);

        Double valueD = 0.0;
        if (Utils.isNumeric(propertys.get(0).toString(), false)) {
            valueD = Double.valueOf(propertys.get(0).toString());
        } else {
            Object obj = getPropertyFormat(propertys.get(0).toString(), propertyList) == null ? null
                    : getValue(property, propertys.get(0).toString());
            valueD = obj == null ? 0.0 : ((Number) obj).doubleValue();
        }

        for (int i = 0; i < oprs.size(); i++) {
            Double valueDtemp = null;
            if (Utils.isNumeric(propertys.get(i + 1).toString(), false)) {
                valueDtemp = Double.valueOf(propertys.get(i + 1).toString());
            } else {
                Object obj = getPropertyFormat(propertys.get(0).toString(), propertyList) == null ? null
                        : getValue(property, propertys.get(0).toString());
                valueDtemp = obj == null ? 0.0 : ((Number) obj).doubleValue();
            }
            BigDecimal num1 = new BigDecimal(valueD == null ? 0 : valueD.doubleValue());
            BigDecimal num2 = new BigDecimal(valueDtemp == null ? 0 : valueDtemp.doubleValue());
            if ("-".equals(oprs.get(i).toString())) {
                num1 = num1.subtract(num2);
                valueD = new Double(num1.doubleValue());
            }
            if ("+".equals(oprs.get(i).toString())) {
                num1 = num1.add(num2);
                valueD = new Double(num1.doubleValue());
            }
            if ("*".equals(oprs.get(i).toString())) {
                num1 = num1.multiply(num2);
                valueD = new Double(num1.doubleValue());
            }
            if ("/".equals(oprs.get(i).toString())) {
                num1 = num1.divide(num2, 10, BigDecimal.ROUND_HALF_UP);
                valueD = new Double(num1.doubleValue());
            }
        }
        NumberFormat numberFormat = new DecimalFormat("0.00");
        return numberFormat.format(valueD);

    }

    private static PropertyFormat getPropertyFormat(String key, List<PropertyFormat> propertyList) {
        for (PropertyFormat pf : propertyList) {

            if (pf.getPropertyName().equals(key)) {
                return pf;
            }
        }
        return null;
    }

    private static void splitOpr(String expression, List propertys, List oprs) {

        char[] oprmodel = {'+', '-', '*', '/'};
        int pos = 0;

        while (true) {

            int posnew = -1;
            String opr = "";

            for (int i = 0; i < oprmodel.length; i++) {
                int pos1 = expression.indexOf(oprmodel[i], pos);
                if (pos1 != -1 && (pos1 < posnew || posnew == -1)) {
                    posnew = pos1;
                    opr = String.valueOf(oprmodel[i]);
                }
            }

            if (posnew == -1) {
                propertys.add(expression.substring(pos));
                break;
            } else {
                propertys.add(expression.substring(pos, posnew));
                oprs.add(String.valueOf(opr));
                pos = posnew + 1;
            }
        }
    }


    public static Object getValue(Object dataObj, String propertyName) throws ExportDataException {
        Object ret = "";
        try {
            propertyName = propertyName.trim();
            if (dataObj instanceof HashMap) {
                ret = ((HashMap<String, Object>) dataObj).get(propertyName);
            } else {
                // 修改为可从继承中取属性值
                ret = org.apache.commons.beanutils.PropertyUtils.getNestedProperty(dataObj, propertyName);
            }


        } catch (Exception e) {
            throw new ExportDataException(e);
        }

        return ret;
    }

    public static String formatData(PropertyFormat propertyFormat, Object value)
            throws ExportDataException {
        if (value == null) {
            return "";
        }
        if (propertyFormat.getFormat() == null) {
            return value.toString();
        } else if (propertyFormat.getFormat().equals("DATE")) {
            // need add
            if (value instanceof java.sql.Timestamp) {
                java.sql.Timestamp date = (java.sql.Timestamp) value;
                DateFormat format = new SimpleDateFormat(
                        propertyFormat.getFormatType());
                return format.format(date);
            }
            if (value instanceof java.util.Date) {
                java.util.Date util = (java.util.Date) value;
                DateFormat format = new SimpleDateFormat(
                        propertyFormat.getFormatType());
                return format.format(util);
            } else {
                return value.toString();
            }
        } else if (propertyFormat.getFormat().equals("NUMBER")) {
            NumberFormat numberFormat = new DecimalFormat(
                    propertyFormat.getFormatType());
            double strdouble;
            if (value.getClass() == String.class) {
                if ("".equals(value)) {
                    value = "0.00";
                }
                strdouble = Double.valueOf((String) value).doubleValue();
                return numberFormat.format(strdouble);
            }
            return numberFormat.format(value);
        } else if (propertyFormat.getFormat().equals("CODE2NAME")) {
            return codeToName(propertyFormat.getFormatType(), String.valueOf(value));
        } else if (propertyFormat.getFormat().equals("MORECODE2NAME")) {
            return moreCodeToName(propertyFormat.getPropertyName(), String.valueOf(value));
        } else if (propertyFormat.getFormat().equals("RATE")) {
            NumberFormat numberFormat = new DecimalFormat(
                    propertyFormat.getFormatType());
            double strdouble;
            if (value.getClass() == Double.class) {
                if ("".equals(value)) {
                    value = "0.00";
                }
                strdouble = ((Double) value).doubleValue();
                return numberFormat.format(strdouble * 100) + "%";
            }
            return numberFormat.format(value);
        } else {
            return value.toString();
        }

    }

    private static String codeToName(String formatType, String code) {
//        return Code2NameUtils.code2Name(formatType,
//                String.valueOf(code), user.getCityid());
//        prodid;产品编码|lsitpricedesc;资费说明|chargeitemid;营业费编码|recfee;营业费标价|rentfee;月租费标价|paytype;支付方式;CODE2NAME;PC_PAYTYPE|billingitemid;计费ID;CODE2NAME;#Billingtarif|isdetail;是否有营业费列账明细;CODE2NAME;$PC_YESNO
//        operid;工号|operid;姓名;CODE2NAME;#OPERATOR|orgid;审核渠道|orgid;渠道名称;CODE2NAME;#WAYIDINFO|begintime;生效日期;DATE;yyyy-MM-dd|endtime;失效日期;DATE;yyyy-MM-dd|modtime;更新时间;DATE;yyyy-MM-dd HH:mm:ss

        try {
            if (translatable == null) {
                translatable = ApplicationUtil.getBean(Translatable.class);
                code = translatable.code2Name(formatType, code);
            } else {
                code = translatable.code2Name(formatType, code);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return code;
    }

    private static String moreCodeToName(String propertyName, String code) {
        return "";
    }

}
