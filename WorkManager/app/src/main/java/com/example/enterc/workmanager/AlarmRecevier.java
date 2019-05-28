package com.example.enterc.workmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmRecevier extends BroadcastReceiver {
    int reCode = 0;
    int noId   = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("nameBundle");// Nhân dữ liệu từ màn hình khác chuyển tới
        String subject= bundle.getString("nameSubject");// Lấy ra dữ liệu
        NotificationHelper notificationHelper = new NotificationHelper(context); // Khởi tạo 1 đối tượng để thiết lập thông báo
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(noId,reCode, subject);// Thiết lập thông báo
    }
}
