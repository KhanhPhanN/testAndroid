package com.example.enterc.workmanager;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Job implements Serializable{
    private int Id;
    private String date;
    private String time_start;
    private String time_end;
    private String subject;
    private String content;
    private boolean isComplete;
    public Job(int Id,String date, String time_start, String time_end, String subject, String content, boolean isComplete) {
        this.Id         = Id;
        this.date       = date;
        this.time_start = time_start;
        this.time_end   = time_end;
        this.subject    = subject;
        this.content    = content;
        this.isComplete = isComplete;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        String s = "\n"+Id+"\n"+date+"\n"+time_start+"--"+time_end+"\n"+subject+"\n"+content+"\n";
        return s;
    }
}
