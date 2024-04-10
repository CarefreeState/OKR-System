package com.macaku.xxljob.executor.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CronUtil {

    public static String getCorn(Date date) {
        return new SimpleDateFormat("s m H d M ? yyyy").format(date);
    }

    public static String getCorn(LocalDateTime startTime, long interval, TimeUnit timeUnit) {
        String cronExpression = "";
        switch (timeUnit) {
            case SECONDS:
                cronExpression = String.format("0/%d * * * * ? *", interval);
                break;
            case MINUTES:
                cronExpression = String.format("%d 0/%d * * * ? *", startTime.getSecond(), interval);
                break;
            case HOURS:
                cronExpression = String.format("%d %d 0/%d * * ? *", startTime.getSecond(), startTime.getMinute(), interval);
                break;
            case DAYS:
                cronExpression = String.format("%d %d %d 1/%d * ? *", startTime.getSecond(), startTime.getMinute(), startTime.getHour(), interval);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit. Please use minutes, hours, or days.");
        }
        return cronExpression;
    }

    public static String getCronWeek(int week) {
        // 星期日为第一天
        if(week <= 0 || week > 7) {
            return null;
        }
        week = week + 1;
        week = week == 8 ? 1 : week;
        return String.format("59 23 23 ? * %d *", week);
    }

    public static void main(String[] args) {
        System.out.println(getCorn(LocalDateTime.now(), 5, TimeUnit.MINUTES));
        System.out.println(getCorn(LocalDateTime.now(), 1, TimeUnit.MINUTES));
        System.out.println(getCronWeek(7));
        System.out.println(getCorn(new Date()));
    }

}
