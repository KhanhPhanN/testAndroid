package com.example.enterc.workmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmNotification extends ContextWrapper{
    public AlarmNotification(Context base) {
        super(base);
    }
    // Tạo thông báo cho công việc
    public void Notification(Job job, Context context){
        Calendar calendar = Calendar.getInstance(); // Khai báo và gán giá trị cho đối tượng Calendar
        AlarmManager alarmManager; // Khai báo đối tượng AlarmManager
        PendingIntent pendingIntent; // Khai báo đối đượng PendingIntent để khi ứng dụng tắt nó vẫn chạy ngầm
        String[] arrDay = job.getDate().split("/");// Lấy ra ngày tháng năm đặt thông báo
        String[] arrTime = job.getTime_start().split(":");// Lấy ra giờ và phút đặt thông báo
        final int y = Integer.parseInt(arrDay[2]);    // Lấy ra năm
        final int m = Integer.parseInt(arrDay[1])-1;  // Lấy ra tháng
        final int d = Integer.parseInt(arrDay[0]);    // Lấy ra ngày
        int h       = Integer.parseInt(arrTime[0]);   // Lấy ra giờ
        int mi      = Integer.parseInt(arrTime[1]);   // Lấy ra phút
        calendar.set(y,m,d,h,mi); // Đặt lại lịch là thời gian bắt đầu của công việc
        int codePending = Integer.parseInt(arrDay[0]+arrDay[1]+arrTime[0]+arrTime[1]); // Tạo mã cho PendingIntent
        // Thông báo công việc gần nhất
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);// Gán giá trị cho đối tượng alarmManager
        Intent intentAlarm = new Intent(context,AlarmRecevier.class);// Tạo Intent để chuyển tiếp giữa các màn hình
        Bundle bundle = new Bundle(); // Khởi tạo Bundle để chứa dữ liệu
        bundle.putString("nameSubject",job.getSubject()); // Thêm dữ liệu vào Bundle
        intentAlarm.putExtra("nameBundle", bundle);// Thêm Bundle vào Intent
        pendingIntent = PendingIntent.getBroadcast(context, codePending, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);// Thiết lập thông số cho đối tượng pendingIntent
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);// Đặt sự kiện cho đối tượng AlarmManager
    }
    // Hủy thông báo 1 công việc
    public void delNotification(Job job, Context context){
        AlarmManager alarmManager;// Khởi tạo đối tượng
        PendingIntent pendingIntent; // Khởi tạo đối tượng
        // Hủy thông báo công việc
        String[] arrDay    = job.getDate().split("/");// Lấy ra ngày tháng năm của công việc
        String[] arrTime   = job.getTime_start().split(":");// Lấy ra thời gian của công việc
        int code           = Integer.parseInt(arrDay[0]+arrDay[1]+arrTime[0]+arrTime[1]);// Tạo mã cho đối tượng pendingIntent
        alarmManager       = (AlarmManager) getSystemService(ALARM_SERVICE); // Gán giá trị cho đối tượng alarmManager
        Intent intentAlarm = new Intent(context,AlarmRecevier.class);// Khởi tạo đói tượng Intent
        pendingIntent      = PendingIntent.getBroadcast(context, code, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);// Thiết lập các thông số cho pendingIntent
        alarmManager.cancel(pendingIntent); // Hủy pendingIntent có mã code
    }
}
