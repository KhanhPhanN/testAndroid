package com.example.enterc.workmanager;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;
    private NotificationManager mManager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(int noId, int reCode, String s) {
        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this);
        // Thông báo sẽ tự động bị hủy khi người dùng click vào Panel
        notBuilder.setAutoCancel(true);
        // --------------------------
        // Chuẩn bị một thông báo
        // --------------------------
        notBuilder.setSmallIcon(R.mipmap.calender);
        notBuilder.setTicker("This is a ticker");
        // Sét đặt thời điểm sự kiện xẩy ra.
        // Các thông báo trên Panel được sắp xếp bởi thời gian này.
        notBuilder.setWhen(System.currentTimeMillis()+ 10* 1000);
        notBuilder.setContentTitle("Nhắc nhở");
        notBuilder.setContentText("Có 1 công việc hiện tại: "+s);
        // Tạo một Intent
        Intent intent = new Intent(this, MainActivity.class);
        // PendingIntent.getActivity(..) sẽ start mới một Activity và trả về
        // đối tượng PendingIntent.
        // Nó cũng tương đương với gọi Context.startActivity(Intent).
        PendingIntent pendingIntent = PendingIntent.getActivity(this, reCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notBuilder.setContentIntent(pendingIntent);
        // Lấy ra dịch vụ thông báo (Một dịch vụ có sẵn của hệ thống).
        NotificationManager notificationService  = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        // Xây dựng thông báo và gửi nó lên hệ thống.
        Notification notification =  notBuilder.build();
        notificationService.notify(noId, notification);
        return  notBuilder;
    }
}