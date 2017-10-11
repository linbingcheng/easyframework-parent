package top.bingchenglin.easyframework.commons.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {
        // private
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static boolean isValidTimestamp(String source, String pattern) {
        try {
            new SimpleDateFormat(pattern).parse(source);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static Timestamp parseTimestamp(String source) {
        return parseTimestamp(source, DateFormatType.DATE_FORMAT_STR);
    }

    public static Timestamp parseTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Timestamp parseTimestamp(String source, String pattern) {
        Timestamp date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            date = new Timestamp(dateFormat.parse(source).getTime());
        } catch (ParseException e) {
            LOGGER.error("DateFormat parse failure!", e);
        }
        return date;
    }

    public static Timestamp parseTimestamp(String source, DateFormatType dateFormatType) {
        return parseTimestamp(source, dateFormatType.getValue());
    }

    public static String formatTimestamp(Timestamp date) {
        return formatTimestamp(date, DateFormatType.DATE_FORMAT_STR);
    }

    public static String formatTimestamp(Timestamp date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String source = dateFormat.format(date);
        return source;
    }

    public static String formatTimestamp(Timestamp date, DateFormatType dateFormatType) {
        return formatTimestamp(date, dateFormatType.getValue());
    }

    public static Timestamp addDay(Timestamp date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DATE, day);
        date = new Timestamp(calendar.getTimeInMillis());
        return date;
    }

    public static Timestamp addMinute(Timestamp date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MINUTE, minute);
        date = new Timestamp(calendar.getTimeInMillis());
        return date;
    }

    public static Timestamp addSeconds(Timestamp date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.SECOND, seconds);
        date = new Timestamp(calendar.getTimeInMillis());
        return date;
    }

    //获取当月第一天
    public static String getCurrentMonthFirst(DateFormatType dateFormatType) throws Exception {
        GregorianCalendar currCal = (GregorianCalendar) Calendar.getInstance();
        currCal.set(Calendar.DATE, 1);    //把日期设置为当月第一天
        return (String) opearationDate(currCal.getTime(), dateFormatType.getValue());
    }

    //获取当月最后一天
    public static String getCurrentMonthEndDay(DateFormatType dateFormatType) throws Exception {
        GregorianCalendar currCal = (GregorianCalendar) Calendar.getInstance();
        currCal.set(Calendar.DATE, 1);        //把日期设置为当月第一天
        currCal.roll(Calendar.DAY_OF_MONTH, -1);
        return (String) opearationDate(currCal.getTime(), dateFormatType.getValue());
    }

    //获取下月最后一天
    public static String getNextMonthEndDay(DateFormatType dateFormatType) throws Exception {
        Calendar nextCal = Calendar.getInstance();
        nextCal.set(Calendar.DATE, 1);        //把日期设置为当月第一天
        nextCal.add(Calendar.MONTH, 1);    //加一个月
        nextCal.roll(Calendar.DAY_OF_MONTH, -1);
        return (String) opearationDate(nextCal.getTime(), dateFormatType.getValue());
    }

    //获取下月第一天
    public static String getNextMonthFirst(DateFormatType dateFormatType) throws Exception {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(lastDate.get(Calendar.YEAR), lastDate.get(Calendar.MONTH), 1, 0, 0, 0);    //把日期设置为当月第一天
        lastDate.add(Calendar.MONTH, 1);    //加一个月
        return (String) opearationDate(lastDate.getTime(), dateFormatType.getValue());
    }

    //获取下月第一天
    public static Date getNextMonthFirst() throws Exception {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(new Date());
        lastDate.set(Calendar.DATE, 1);        //把日期设置为当月第一天
        lastDate.add(Calendar.MONTH, 1);    //加一个月
        System.err.println("========DateUtil.getNextMonthFirst()=========" + lastDate.getTime());
        return lastDate.getTime();
    }

    //获取下个月结日
    public static String getNextSettelday(int settelday, DateFormatType dateFormatType) throws Exception {
        return (String) opearationDate(getNextSettelday(settelday), dateFormatType.getValue());
    }

    //获取下个月结日
    public static Timestamp getNextSettelday(int settelday) throws Exception {
        Calendar date = Calendar.getInstance();
        int month = date.get(Calendar.MONTH);
        if (date.get(Calendar.DATE) >= settelday) {
            month = month + 1;
        }
        date.set(date.get(Calendar.YEAR), month, settelday, 0, 0, 0);
        if (date.get(Calendar.MONTH) > month) {
            date.add(Calendar.DAY_OF_MONTH, -1 * date.get(Calendar.DAY_OF_MONTH));
        }
        return parseTimestamp(date.getTime());
    }

    private static Object opearationDate(Object object, String formatStr) {
        if (object == null || null == formatStr || "".equals(formatStr)) {
            throw new RuntimeException("参数不能为空");
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            if (object instanceof Date)
                return format.format(object);
            else
                return format.parse(object.toString());
        } catch (Exception e) {
            throw new RuntimeException("操作失败", e);
        }

    }

    public enum DateFormatType {
        /**
         * 格式为：yyyy-MM-dd HH:mm:ss
         */
        DATE_FORMAT_STR_CHINE("MM月dd日HH时mm分"),
        /**
         * 格式为：yyyy-MM-dd HH:mm:ss
         */
        DATE_FORMAT_STR("yyyy-MM-dd HH:mm:ss"),
        /**
         * 格式为：yyyy-MM-dd HH:mm:ss
         */
        DATE_FORMAT_STR_MS("yyyy-MM-dd HH:mm:ss.SSS"),
        /**
         * 格式为：yyyyMMddHHmmss
         */
        SIMPLE_DATE_TIME_FORMAT_STR("yyyyMMddHHmmss"),

        /**
         * 格式为：yyyyMMddHHmmssSSS
         */
        SIMPLE_DATE_TIME_MICROSECONDS_FORMAT_STR("yyyyMMddHHmmssSSS"),

        /**
         * 格式为：yyyy-MM-dd
         */
        SIMPLE_DATE_FORMAT_STR("yyyy-MM-dd"),

        /**
         * 格式为：yyyyMMdd
         */
        SIMPLE_DATE_FORMAT_COMMON_STR("yyyyMMdd"),

        /**
         * 格式为：yyyyMM
         */
        SIMPLE_DATE_FORMAT_YEAR_MON("yyyyMM"),

        /**
         * 格式为：yyyy/MM/dd
         */
        SIMPLE_DATE_FORMAT_VIRGULE_STR("yyyy/MM/dd"),

        /**
         * 格式为：HH:mm:ss
         */
        HOUR_MINUTE_SECOND("HH:mm:ss"),

        /**
         * 格式为：yyyy-MM
         */
        YEAR_MONTH_STR("yyyy-MM"),

        /**
         * 格式为：HHmmss
         */
        YEARMONTH("HHmm"),

        /**
         * 格式为：HH:mm
         */
        HOUR_MINUTE("HH:mm");

        private final String value;

        DateFormatType(String formatStr) {
            this.value = formatStr;
        }

        public String getValue() {
            return value;
        }
    }

}
