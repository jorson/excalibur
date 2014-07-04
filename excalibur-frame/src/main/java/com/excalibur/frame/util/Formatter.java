package com.excalibur.frame.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.TimeZone;

/**
 * Formatter
 * Date: 14-3-12
 *
 * @author Yangz
 */
public class Formatter {

    private static final String   DATE_FORMAT       = "yyyy-MM-dd";
    private static final String   TIME_FORMAT       = "yyyy-MM-dd HH:mm:ss";
    private static final String   DEFAULT_TIME_ZONE = "GMT+8";
    private static       TimeZone timeZone          = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);

    static {
        TimeZone.setDefault(timeZone);
    }

    public static void setTimeZone(TimeZone zone) {
        if (zone == null) {
            return;
        }
        TimeZone.setDefault(zone);
        timeZone = zone;
    }

    public static String fromDate(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return DateFormatUtils.format(date, DATE_FORMAT);
    }

    public static String fromDateToTime(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return DateFormatUtils.format(date, TIME_FORMAT);
    }

}
