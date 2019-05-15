package com.example.enterc.workmanager;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class InputCheck {

    public int isValidTime(String time){
        String[] list = time.split(":");
        if(list.length!=2){
            return 1;
        }else if(Integer.parseInt(list[0])>23 || Integer.parseInt(list[0])<0){
            return 2;
        }else if(Integer.parseInt(list[1])>59 || Integer.parseInt(list[1])<0){
            return 3;
        }
        return 0;
    }

    public boolean isEndthanStart(String time_start, String time_end){
        String[] list_1 = time_start.split(":");
        String[] list_2 = time_end.split(":");
        if(isValidTime(time_start)==0 && isValidTime(time_end)==0){
            if(Integer.parseInt(list_1[0]) >  Integer.parseInt(list_2[0])){
                return false;
            }else if(Integer.parseInt(list_1[0]) ==  Integer.parseInt(list_2[0]) && Integer.parseInt(list_1[1]) >  Integer.parseInt(list_2[1])){
                return false;
            }
        }

        return  true;
    }

     Pattern dateRegexPattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)");

    public  boolean isValidDate(String dateString) {
        Matcher dateMatcher = dateRegexPattern.matcher(dateString);

        if (dateMatcher.matches()) {

            dateMatcher.reset();

            if (dateMatcher.find()) {
                String day = dateMatcher.group(1);
                String month = dateMatcher.group(2);
                int year = Integer.parseInt(dateMatcher.group(3));

                if ("31".equals(day) &&
                        ("4".equals(month) || "6".equals(month) || "9".equals(month) ||
                                "11".equals(month) || "04".equals(month) || "06".equals(month) ||
                                "09".equals(month))) {
                    return false; // 1, 3, 5, 7, 8, 10, 12 has 31 days
                } else if ("2".equals(month) || "02".equals(month)) {
                    //leap year
                    if (year % 4 == 0) {
                        return !"30".equals(day) && !"31".equals(day);
                    } else {
                        return !"29".equals(day) && !"30".equals(day) && !"31".equals(day);
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
