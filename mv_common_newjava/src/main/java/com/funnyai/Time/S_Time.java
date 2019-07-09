package com.funnyai.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class S_Time {

    public static void main(String[] args) {
        System.out.println(formatYMD2(now()));
    }
    
    public static String yyyyMMdd(int days)
    {
        Calendar day=Calendar.getInstance();
        day.add(Calendar.DATE,days);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        return sdf.format(day.getTime());
    }

    public static Date getDateFrom_YMD(String str_yyyy_MM_dd) {

        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = bartDateFormat.parse(str_yyyy_MM_dd);
            return date;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static Date getDateFrom_YMDHMS(String str_yyyy_MM_dd_HH_mm_ss) {

        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = bartDateFormat.parse(str_yyyy_MM_dd_HH_mm_ss);
            return date;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    public static String now_HH_mm() {
        Date date = new Date();
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("HH:mm");
        return bartDateFormat.format(date);
    }

    public static String now_YMD_Hms() {
        Date date = new Date();
        return formatYMD_Hms(date);
    }

    public static String now_YMD() {
        Date date = new Date();
        return formatYMD(date);
    }
    
    public static String now_YMD_HM() {
        Date date = new Date();
        return formatYMD_HM(date);
    }

    public static Date now() {
        return new Date();
    }

    public static long getTicksFromDate(Date date) {
        int year = 1970;
        long days = (year - 1) * 365 + (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400;

        long time0 = days * 24 * 60 * 60 * 1000 * 1000 * 10;

        long time = date.getTime() * 1000 * 10;

        long time3 = 8;//加上第8时区.
        time3 = time3 * 60 * 60 * 1000 * 1000 * 10;

        return time + time0 + time3;
    }

    public static Date getDateFromTicks(long ticks) {

        Date date = new Date();
        date.setTime(0);
        long ticks1970 = getTicksFromDate(date);

        long time = (ticks - ticks1970) / 10 / 1000;

        date.setTime(time);

        return date;
    }

    public static String formatYMD_HM(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return bartDateFormat.format(date);
    }
    
    public static String formatYMD(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date();
        return bartDateFormat.format(date);
    }

    public static String formatYMD2(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-M-d");
        return bartDateFormat.format(date);
    }

    public static String formatYMD_Hms(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return bartDateFormat.format(date);
    }
    
    public static String format_custom(Date date,String strFormat) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(strFormat);//"yyyy-MM-dd HH:mm:ss");
        return bartDateFormat.format(date);
    }

    public static Date addDays2(Date d, int iDays) {
        try {
            Date dReturn = new Date();
            dReturn.setTime(d.getTime());
            long myTime = (d.getTime() / 1000) + iDays * 24 * 60 * 60;
            dReturn.setTime(myTime * 1000);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mdate = format.format(dReturn);
            return getDateFrom_YMDHMS(mdate);
        } catch (Exception e) {
            return d;
        }
    }

    public static Date add_days(Date pDate,int iDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.add(Calendar.DAY_OF_MONTH, iDays);//-1);
        return c.getTime();
    }
    
    public static Date add_months(Date pDate,int iMonths) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.add(Calendar.MONTH, iMonths);//-1);
        return c.getTime();
    }
    
    
    public static Date add_hours(Date pDate,int ihours) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.add(Calendar.HOUR, ihours);//-1);
        return c.getTime();
    }
    
    
    public static int GetHour(Date pDate){
        
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.HOUR_OF_DAY);
    }
    
    public static int GetMinute(Date pDate){
        
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.MINUTE);
    }
    
    public static int GetSecond(Date pDate){
        
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.SECOND);
    }
    
    public static int Get_Milli_Second(Date pDate){
        
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.MILLISECOND);
    }
    
    
    public static int GetYear(Date pDate){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.YEAR);
    }
    
    public static int GetMonth(Date pDate){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.MONTH)+1;
    }
    
    public static int GetDate(Date pDate){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int GetDate_OF_Week(Date pDate){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(pDate);
        return rightNow.get(Calendar.DAY_OF_WEEK)-1;
    }
}
