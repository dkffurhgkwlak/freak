package com.my.example;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Simple {
    //@Test
    public void test1() throws Exception{
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("localDateTime : " + localDateTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = simpleDateFormat.format(localDateTime);
        System.out.println(dateString);
// Create two LocalDateTime objects
        LocalDateTime dateTime1 = LocalDateTime.of(2021, 5, 1, 10, 30, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2021, 5, 2, 11, 0, 0);

// Compare the two LocalDateTime objects using the compareTo() method
        if(dateTime1.compareTo(dateTime2) < 0) {
            System.out.println("dateTime1 is before dateTime2");
        } else if(dateTime1.compareTo(dateTime2) > 0) {
            System.out.println("dateTime1 is after dateTime2");
        } else {
            System.out.println("dateTime1 is equal to dateTime2");
        }
    }
    @Test
    public void test(){
        List<String> list = Arrays.asList("a", "b", "c");
        String quotedString = "\"" + String.join("\",\"", list) + "\"";
        System.out.println(quotedString);
    }
}
