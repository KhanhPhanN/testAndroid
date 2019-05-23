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
        Bundle bundle = intent.getBundleExtra("nameBundle");
        String subject= bundle.getString("nameSubject");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(noId,reCode, subject);
        reCode++;
        noId++;
        //notificationHelper.getManager().notify(1, nb.build());
    }
}
