package com.maahi.spry.UtilsService;

import java.util.Calendar;

public class DateUtils {

    public boolean isValidYear(String yearValue) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int year = Integer.parseInt(yearValue);
        return year >= 1500 && year <= currentYear;
    }
}
