package com.example.thanh.instagram.utils;

import java.sql.Timestamp;
import java.util.Date;

public class TimeUtils {

    // == constructor ==
    private TimeUtils() {
    }

    // == public methods ==
    public static String dateOfImage() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis()); // get the specific time. (EX: 87546987) not readable.
        return timestamp.toString();
    }

    public static String currentReadableTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime()); // readable (EX: 4/2/2018)
        return date.toString();
    }
}
