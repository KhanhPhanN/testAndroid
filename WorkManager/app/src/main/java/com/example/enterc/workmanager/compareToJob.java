package com.example.enterc.workmanager;

import java.util.Comparator;

public class compareToJob implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {
        String x1 = o1.getTime_start();
        String y1 = o1.getTime_end();
        String x2 = o2.getTime_start();
        String y2 = o2.getTime_end();
        if(x1.equals(x2) && y1.equals(y2))
        return 0;
        else if(x1.compareTo(x2) > 0 || (x1.equals(x2) && y1.compareTo(y2) > 0))
        return 1;
        else
        return -1;
    }
}
