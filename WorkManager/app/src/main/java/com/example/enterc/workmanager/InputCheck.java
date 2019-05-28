package com.example.enterc.workmanager;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class InputCheck {

    public int isValidTime(String time){// Kiểm tra thời gian có hợp lệ không
        String[] list = time.split(":");// Cắt chuỗi chia thành giờ và phút
        if(list.length!=2){// Nếu không gồm đủ thời gian là giờ và phút
            return 1;// Trả về lỗi 1
        }else if(Integer.parseInt(list[0])>23 || Integer.parseInt(list[0])<0){// Nếu giờ lớn hơn 23 hoặc nhỏ hơn 0
            return 2;// Trả về lỗi 2
        }else if(Integer.parseInt(list[1])>59 || Integer.parseInt(list[1])<0){// Nếu phút lớn hơn 59 hoặc nhỏ hơn 0
            return 3;// Trả về lỗi 3
        }
        return 0;// Nếu định dang đúng trả về 0
    }

    public boolean isEndthanStart(String time_start, String time_end){// Kiểm tra thời gian bắt đầu có lớn hơn thời gian kết thúc không
        String[] list_1 = time_start.split(":");// Cắt lấy giờ và phút bắt đầu công việc
        String[] list_2 = time_end.split(":");// Cắt lấy giờ và phút kết thúc công việc
        if(isValidTime(time_start)==0 && isValidTime(time_end)==0){// Kiểm tra định dạng thời gian nếu đúng
            if(Integer.parseInt(list_1[0]) >  Integer.parseInt(list_2[0])){// Kiểm tra nếu giờ của thời gian bắt đầu lớn hơn giờ của thời gian kết thúc
                return false;// Trả về false
            }else if(Integer.parseInt(list_1[0]) ==  Integer.parseInt(list_2[0]) && Integer.parseInt(list_1[1]) >  Integer.parseInt(list_2[1])){ // Nếu giờ như nhau nhưng phút của thời gian bắt đầu lớn hơn phút của thời gian kết thúc
                return false;// Trả về false
            }
        }

        return  true;// Nếu thời gian bắt đầu nhỏ hơn thời gian kết thúc trả về true
    }

     Pattern dateRegexPattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)");// Định dạng kiểm tra ngày tháng năm có hợp lệ hay không, chỉ bao gồm số

    public  boolean isValidDate(String dateString) {// Kiểm tra định dạng thời gian
        Matcher dateMatcher = dateRegexPattern.matcher(dateString);// Kiểm tra chuỗi ngày tháng
        if (dateMatcher.matches()) {// Kiểm tra ngày có chuỗi hợp lệ với regular expression
            dateMatcher.reset();
            if (dateMatcher.find()) {// Tìm kiếm trong chuỗi ngày tháng người dùng nhập vào
                String day = dateMatcher.group(1);// Lấy ra ngày
                String month = dateMatcher.group(2);// Lấy ra tháng
                int year = Integer.parseInt(dateMatcher.group(3));// Lấy ra năm

                if ("31".equals(day) &&
                        ("4".equals(month) || "6".equals(month) || "9".equals(month) ||
                                "11".equals(month) || "04".equals(month) || "06".equals(month) ||
                                "09".equals(month))) {// Nếu trong các tháng 4,6,9,11 có 31 ngày
                    return false; // 1, 3, 5, 7, 8, 10, 12 has 31 days
                } else if ("2".equals(month) || "02".equals(month)) {// Kiểm tra tháng 2 chỉ có 28 hoặc 29 ngày
                    if (year % 4 == 0) {// Nếu là năm nhuận
                        return !"30".equals(day) && !"31".equals(day); // Trả về false nếu có 30 hoặc 31 ngày
                    } else {// Nếu không phải năm nhuận
                        return !"29".equals(day) && !"30".equals(day) && !"31".equals(day);// Trả về false nếu có 30 hoặc 29 ngày
                    }
                } else {
                    return true;// Trả về true nếu tháng 2 có 28 ngày nếu là năm thường, 29 ngày nếu là năm nhuận
                }
            } else {
                return false;// Trả về false nếu không tìm thấy chuỗi thể hiện ngày tháng hoặc năm
            }
        } else {
            return false;// Trả về false nếu không tìm thấy chuỗi thể hiện ngày tháng hoặc năm
        }
    }
}
