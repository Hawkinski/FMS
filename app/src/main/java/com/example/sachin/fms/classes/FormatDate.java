package com.example.sachin.fms.classes;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class FormatDate {

    public String FormatDate(String str) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

        DateFormat format2 = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

        Log.e("DATEEEE", str);

        String newDate = "";
        Date d;
        try {
            d = format.parse(str);
            newDate = format2.format(d);

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());


        }
        return newDate;
    }
}
