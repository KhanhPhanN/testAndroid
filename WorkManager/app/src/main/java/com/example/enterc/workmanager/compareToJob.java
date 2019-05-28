package com.example.enterc.workmanager;

import java.util.Comparator;

public class compareToJob implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {// So sánh hai công việc
        String x1 = o1.getTime_start();// Lấy ra thời gian bắt đầu của công việc đầu tiên
        String y1 = o1.getTime_end();// Lấy ra thời gian kết thúc của công việc đầu tiên
        String x2 = o2.getTime_start();// Lấy ra thời gian bắt đầu của công việc thứ hai
        String y2 = o2.getTime_end();// Lấy ra thời gian kết thúc của công việc thứ hai
        if(x1.equals(x2) && y1.equals(y2))// Nếu thời gian bắt đầu và kết thúc của hai công việc như nhau
        return 0;// Trả về 0
        else if(x1.compareTo(x2) > 0 || (x1.equals(x2) && y1.compareTo(y2) > 0))// Nếu giờ của công việc 1 lớn hơn công việc 2 hoặc nếu 2 giờ bằng nhau nhưng phút công việc 1 lớn hơn công việc 2
        return 1;// Trả về 1
        else// Trường họp còn lại
        return -1;// Trả về -1
    }
}
