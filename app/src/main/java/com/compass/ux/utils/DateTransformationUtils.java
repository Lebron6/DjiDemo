package com.compass.ux.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by seven on 2017/11/24.
 * <p>
 * 时间转换类
 */

public class DateTransformationUtils {

    /**
     * 时间+1天
     *
     * @param sdate
     * @return
     */
    public static String getUpDate(String sdate) {
        String dates;
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//        sf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dates = sf.format(cal.getTime());
        return dates;
    }

    /**
     * 时间-1天
     */
    public static String getDownDate(String sdate) {
        String dates;
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//        sf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        dates = sf.format(cal.getTime());
        return dates;
    }

    /**
     * String类型转成date类型(年月日)
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * String类型转成date类型(时分)
     *
     * @param strDate
     * @return
     */
    public static Date stringToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 判断当前日期是周几
     */
    public static String dayForWeek(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        String week = "";
        switch (dayForWeek) {
            case 1:
                week = " 周一";
                break;
            case 2:
                week = " 周二";
                break;
            case 3:
                week = " 周三";
                break;
            case 4:
                week = " 周四";
                break;
            case 5:
                week = " 周五";
                break;
            case 6:
                week = " 周六";
                break;
            case 7:
                week = " 周日";
                break;
            default:
                break;
        }
        return week;
    }

    /**
     * 判断日期是否为今天
     * s1 今天
     * s2 比较日期
     */
    public static boolean isToday(String time) {
        String[] a1 = getTime(System.currentTimeMillis(), "yyyy-MM-dd").split("-");
        String[] a2 = time.split("-");
        if (Integer.parseInt(a1[0]) == Integer.parseInt(a2[0]) && Integer.parseInt(a1[1]) == Integer.parseInt(a2[1]) && Integer.parseInt(a1[2]) == Integer.parseInt(a2[2])) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (TextUtils.isEmpty(date1))
            return 0;
        if (TextUtils.isEmpty(date2))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * long类型数据得到二个日期间的间隔天数
     */
    public static String getTwoDay(long l1, long l2) {
        long day = 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date date = new Date(l1);
            Date mydate = new Date(l2);
            String s1 = myFormatter.format(date);
            String s2 = myFormatter.format(mydate);
            Date dateNew = myFormatter.parse(s1);
            Date mydateNew = myFormatter.parse(s2);
            day = (dateNew.getTime() - mydateNew.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 时分比较时间差绝对值
     */
    public static long getTimeDifference(String date1, String date2) {
        if (TextUtils.isEmpty(date1))
            return 0;
        if (TextUtils.isEmpty(date2))
            return 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = Math.abs(date.getTime() - mydate.getTime());
        return diff;
    }

    /**
     * 时分比较时间差
     */
    public static long getDifference(String date1, String date2) {
        if (TextUtils.isEmpty(date1))
            return 0;
        if (TextUtils.isEmpty(date2))
            return 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = date.getTime() - mydate.getTime();
        return diff;
    }

    /**
     * 比较两个时间大小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDateTime(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return false;
            } else if (dt1.getTime() < dt2.getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 比较两个时间大小HotelCreateOrderActivity专用
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDateTimeWithEqual(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return false;
            } else if (dt1.getTime() <= dt2.getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * 比较两个日期大小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.before(dt2)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  false;
    }

    /**
     * 时间是否属于指定时间范围内
     *
     * @param date1 最小时间
     * @param date2 最大时间
     * @param date3 需要比较的时间
     * @return
     */
    public static boolean isTimeRange(String date1, String date2, String date3) {
        if (TextUtils.isEmpty(date1))
            return false;
        if (TextUtils.isEmpty(date2))
            return false;
        if (TextUtils.isEmpty(date3))
            return false;
        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        java.util.Date date_one = null;
        java.util.Date date_two = null;
        java.util.Date date_three = null;
        try {
            date_one = myFormatter.parse(date1);
            date_two = myFormatter.parse(date2);
            date_three = myFormatter.parse(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date_three.getTime() - date_one.getTime() >= 0 && date_three.getTime() - date_two.getTime() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间是否属于指定时间范围内不包括后面的时间
     *
     * @param date1 最小时间
     * @param date2 最大时间
     * @param date3 需要比较的时间
     * @return
     */
    public static boolean isTimeRangeWithoutChooseDate(String date1, String date2, String date3) {
        if (TextUtils.isEmpty(date1))
            return false;
        if (TextUtils.isEmpty(date2))
            return false;
        if (TextUtils.isEmpty(date3))
            return false;
        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
//        myFormatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        java.util.Date date_one = null;
        java.util.Date date_two = null;
        java.util.Date date_three = null;
        try {
            date_one = myFormatter.parse(date1);
            date_two = myFormatter.parse(date2);
            date_three = myFormatter.parse(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date_three.getTime() - date_one.getTime() >= 0 && date_three.getTime() - date_two.getTime() < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 毫秒转成年月日
     *
     * @param time
     * @return
     */
    public static String getYearMonthDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String date = sdf.format(new Date(time));
        return date;
    }

    /**
     * 毫秒转成年月日时分
     *
     * @param time
     * @return
     */
    public static String getYearMonthDayHourMin(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String date = sdf.format(new Date(time));
        return date;
    }






    /**
     * long转成指定样式时间
     */
    public static String getTime(long time, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String date = sdf.format(new Date(time));
        return date;
    }

    /**
     * 计算两个时间间隔月数(国际机票专用)
     */
    public static int getMonthSpace(String date1, String date2) {
        int result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(date1));
            c2.setTime(sdf.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            result = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
            if (result == 6) {
                if (c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) > 0) {
                    result = result + 1;
                }
            }
        } else if (c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR)) {
            result = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) + ((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) * 12);
            if (c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) > 0) {
                if (result == 6) {
                    result = result + 1;
                }
            }
        }

        return result;
    }

    /**
     * 计算年龄
     *
     * @return
     */
    public static Integer getAge(String start, String birth) {
        long l2 = 0;
        if (birth.contains("-")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date br = format.parse(birth);
                l2 = br.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            l2 = Long.valueOf(birth);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date birthDate = new Date(l2);
        try {
            startDate = simpleDateFormat.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        if (calendar.before(birth)) {
            throw new IllegalArgumentException("Now day before birth. It's unable!");
        }

        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(birthDate);

        int birthYear = calendar.get(Calendar.YEAR);
        int birthMonth = calendar.get(Calendar.MONTH);
        int dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - birthYear;

        if (monthNow <= birthMonth) {
            if (monthNow == birthMonth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }

        return age;
    }

    /**
     * 取得当前时间戳（毫秒）
     *
     * @return
     */
    public static long timeStamp() {
        long time = System.currentTimeMillis();
//        String t = String.valueOf(time);
        return time;
    }


    /**
     * 比较时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean compareTime(long startTime, long endTime) {
        String departTime = getTime(startTime, "yyyy-MM-dd HH");
        String arrivalTime = getTime(endTime, "yyyy-MM-dd HH");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
//        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date startDate = format.parse(departTime);
            Date endDate = format.parse(arrivalTime);
            if (startDate.getTime() > endDate.getTime()) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 比较时间 不比小时 订酒店专用 其他别用
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean compareTimeYMD(long startTime, long endTime) {
        String departTime = getTime(startTime, "yyyy-MM-dd");
        String arrivalTime = getTime(endTime, "yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        if (departTime.equals(arrivalTime)) {
            return false;
        }
        try {
            Date startDate = format.parse(departTime);
            Date endDate = format.parse(arrivalTime);
            if (startDate.getTime() > endDate.getTime()) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 比较时间 后面日期小于等于前面 不比小时 火车改签 其他别用
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean compareTimeYMD(String startTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        if (startTime.equals(endTime)) {
            return true;
        }
        try {
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);
            if (startDate.getTime() > endDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 计算时间差小时分钟
     *
     * @param d1
     * @param d2
     * @return
     */
    public static String getHourMinTime(long d1, long d2) {
        java.util.Date now, date;
        now = new Date(d2);
        date = new Date(d1);
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        return hour + "小时" + min + "分钟";
    }

    /**
     * 计算时间差小时
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long getHourTime(long d1, long d2) {
        java.util.Date now, date;
        now = new Date(d1);
        date = new Date(d2);
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        return hour;
    }


    // 将字符串转为时间戳
    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            re_time = String.valueOf(l);
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return re_time;
    }

    /**
     * 把分钟转为时分显示
     */
    public static String getCostTime(String time) {
        int min, h = 0, m = 0;
        try {
            min = Integer.parseInt(time);
            h = min / 60;
            m = min % 60;
        } catch (Exception e) {
        }
        if (m >= 10) {
            return h + "小时" + m + "分";
        } else {
            return h + "小时0" + m + "分";
        }
    }

    /**
     * 时间-分钟
     * @param day  需要减的时间
     * @param minute  减少分钟数
     * @return
     */
    public static String reduceMinute(String day, int minute) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() - (long)minute * 60 * 1000);
        return df.format(newDate2);
    }


}
