package com.example.enterc.workmanager;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Job implements Serializable{
    private int Id;// Id công việc
    private String date;// Ngày thực hiện công việc
    private String time_start;// Thời gian bắt đầu công việc
    private String time_end;// Thời gian kết thúc công việc
    private String subject;// Chủ đề công việc
    private String content;// Nội dung chi tiết công việc
    private boolean isComplete;// Đánh dấu là công việc quan trọng hay không
    public Job(int Id,String date, String time_start, String time_end, String subject, String content, boolean isComplete) {
        this.Id         = Id;// Gán giá trị Id
        this.date       = date;// Gán giá trị ngày
        this.time_start = time_start;// Gán giá trị thời gian bắt đầu công việc
        this.time_end   = time_end;// Gán giá trị thời gian kết thúc công việc
        this.subject    = subject;// Gán giá trị chủ đề công việc
        this.content    = content;// Gán giá trị nội dung chi tiêt công việc
        this.isComplete = isComplete;// Gán giá trị độ quan trọng công việc
    }

    public String getDate() {
        return date;// Trả về ngày thực hiện công việc
    }

    public void setDate(String date) {
        this.date = date;// Thay đổi ngày thực hiện công việc
    }

    public String getTime_start() {
        return time_start;//Trả về thời gian bắt đầu thực hiện công việc
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;// Thay đổi thời gian bắt đầu thực hiện công việc
    }

    public String getTime_end() {
        return time_end;//Trả về thời gian kết thúc thực hiện công việc
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;// Thay đổi thời gian kết thúc thực hiện công việc
    }

    public String getSubject() {
        return subject;// Trả về chủ đề công việc
    }

    public void setSubject(String subject) {
        this.subject = subject;//Thay đổi chủ đề công việc
    }

    public String getContent() {
        return content;// Trả về nội dung chi tiết công việc
    }

    public void setContent(String content) {
        this.content = content;//Thay đổi nội dung chi tiết công việc
    }

    public boolean isComplete() {
        return isComplete;//  Trả về độ quan trọng
    }

    public void setComplete(boolean complete) {
        isComplete = complete;// Đặt lại độ quan trọng
    }

    public int getId() {
        return Id;// Trả về Id của công việc
    }

    public void setId(int id) {
        Id = id;// Đặt lại giá trị Id
    }

    @Override
    public String toString() {
        String s = "\n"+Id+"\n"+date+"\n"+time_start+"--"+time_end+"\n"+subject+"\n"+content+"\n";// Định dạng lại chuỗi khi gọi đến hàm toString()
        return s;// Trả về chuỗi
    }
}
