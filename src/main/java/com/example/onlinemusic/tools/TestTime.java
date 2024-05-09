package com.example.onlinemusic.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestTime {
    public static void main(String[] args) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());
        System.out.println("当前时间： "+time);

        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        String time1 = sf1.format(new Date());
        System.out.println("当前时间： "+time1);
    }
}
