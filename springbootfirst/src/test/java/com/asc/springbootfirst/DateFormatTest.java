package com.asc.springbootfirst;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author:dijian
 * @date:2018/9/21
 */
public class DateFormatTest {
    public static void main(String[] args) {
        String str = "09/20/18 11:43 PM";

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.ENGLISH);

        try {
            System.out.println(dateFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
