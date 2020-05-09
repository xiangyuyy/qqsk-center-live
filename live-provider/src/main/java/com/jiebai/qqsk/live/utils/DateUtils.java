package com.jiebai.qqsk.live.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期时间工具类
 *
 * @author pengzhen
 * @version v1.0.0
 * @date 2019/08/13 08:59:08
 */
public class DateUtils {

  /**
   * 获取当前年份
   * @return
   */
  public static String getSysYear() {
    Calendar date = Calendar.getInstance();
    String year = String.valueOf(date.get(Calendar.YEAR));
    return year;
  }
  /**
   * 字符时间类型转日期
   * @param strDate
   * @return
   */
  public static Date stringToDate(String strDate){
    //注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
    SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
    Date date=null;
    //必须捕获异常
    try {
      date=sDateFormat.parse(strDate);
    } catch(ParseException px) {
      px.printStackTrace();
    }
    return date;
  }
  /**
   * 获取今天
   * @return
   */
  public static String getThisDayTime(){
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(dt);
  }

  /**
   * 获取前第八的日期
   * @return
   */
  public static String getEightDay(){
    Date d=new Date(System.currentTimeMillis()-1000*60*60*24*8);
    SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
    //获取昨天日期
    return sp.format(d);
  }

  /**
   * 获取昨天的日期
   * @return
   */
  public static String getYesterday(){
    Date d=new Date(System.currentTimeMillis()-1000*60*60*24);
    SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
    //获取昨天日期
    return sp.format(d);
  }

  /**
   * 日期排序
   * @param dateList
   * @return
   */
  public static List<Date> getSortDate(List<Date> dateList){
    dateList.sort((a1, a2) -> {
      return a1.compareTo(a2);
    });
    return dateList;
  }

  /**
   * @description: 两个String类型，按照日期格式对比
   *              eg:
   *                  dateOne：2015-12-26
   *                  dateTwo：2015-12-26
   *                  dateFormatType: yyyy-MM-dd
   *                  返回类型：-1：dateOne小于dateTwo， 0：dateOne=dateTwo ，1：dateOne大于dateTwo
   * @param dateOne
   * @param dateTwo
   * @param dateFormatType：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss /等
   * @return -1，0，1，100
   * @throws
   * @data:2019-08-20下午7:41:51
   */
  public static int compareTime(String dateOne, String dateTwo , String dateFormatType){

    DateFormat df = new SimpleDateFormat(dateFormatType);
    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarEnd = Calendar.getInstance();

    try {
      calendarStart.setTime(df.parse(dateOne));
      calendarEnd.setTime(df.parse(dateTwo));
    } catch (ParseException e) {
      e.printStackTrace();
      return 100;
    }
    int result = calendarStart.compareTo(calendarEnd);
    if(result > 0){
      result = 1;
    }else if(result < 0){
      result = -1;
    }else{
      result = 0 ;
    }
    return result ;
  }

  /**
   * 时间戳转换日期
   * @param stamp
   * @return
   */
  public static String stampToTime(Long stamp){
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 时间戳转换日期
    String sd = sdf.format(new Date(stamp));
    return sd;
  }

  /**
   * 获取某个时间num分钟后的时间
   * @param date 日期
   * @param num 分钟数
   * @return Date
   */
  public static Date getAfterMinutesDate(Date date, int num) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, num);
    return calendar.getTime();
  }

    /**
     * 直播时间转成汉字格式
     * @param date 日期
     * @return 汉字
     */
  public static String liveDateToStringWithoutSecond(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    return sdf.format(date);
  }

  /**
   * 获取某日开始时间
   * @param date 传入日期
   * @return 返回该日开始时间 (00:00:00)
   */
  public static Date startDayTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTime();
  }

  /**
   * 获取某日结束时间
   * @param date 传入日期
   * @return 返回该日结束时间 (23:59:59)
   */
  public static Date endDayTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return calendar.getTime();
  }

}
