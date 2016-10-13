package com.getting.util;

public class TimeUtil {

    public static String formatTime(double time) {
        final int hour = ((int) time) / 3600;
        final int left = ((int) time) % 3600;
        final int second = (left % 60);
        final int minute = (left / 60);
        StringBuilder builder = new StringBuilder();
        if (hour > 0) {
            builder.append(hour);
            builder.append("小时");
        }
        if (minute > 0) {
            builder.append(minute);
            builder.append("分");
        }
        if (second > 0) {
            builder.append(second);
            builder.append("秒");
        }
        return builder.toString();
    }

}
