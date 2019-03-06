package com.elefthes.maskswap.util;

import java.util.Calendar;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;


public class DateFormatter{
    /**
    * String型をjava.sql.Date型に変換
    * @param strDate yyyyMMddのフォーマットの文字列
    * @return 変換後のDate。何かしらのエラー時nullを返す。
    */
    public static Date convert(String strDate) {
        java.sql.Date date;
        //nullチェック
        if(strDate == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        // 日付解析を厳密に行う設定に変更
        df.setLenient(false);
        try {
            //日付を解析しDateを取得
            java.util.Date d = df.parse(strDate);
            //Calenderインスタンスを作成し、時間、分、秒、ミリ秒を0に設定
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date = new java.sql.Date(cal.getTimeInMillis());
        } catch(ParseException e) {
            return null;
        }
        return date;
    }
    
    public static String convert(Date date) {
        String str = new SimpleDateFormat("yyyyMMdd").format(date);
        return str;
    }
    
    public static String convertSlash(Date date) {
        String str = new SimpleDateFormat("yyyy/MM/dd").format(date);
        return str;
    }
}
