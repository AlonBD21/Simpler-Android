package alonbd.simpler.TaskLogic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import java.io.Serializable;

import alonbd.simpler.BackgroundAndroid.TasksManager;
import alonbd.simpler.R;

public class NotificationAction implements Action, Serializable {
    public final static String CHANNEL_ID = "NotificationActionChannel";
    private final static CharSequence CHANNEL_NAME = "SimplerTasks";
    final static int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

    private int notificationId;
    private String mContent;
    private String mTaskName;

    public NotificationAction(int notificationId, String mContent, String mTaskName) {
        this.notificationId = notificationId;
        this.mContent = mContent;
        this.mTaskName = mTaskName;
    }

    @Override
    public void onExecute(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, generateNotification(context));
    }

    private Notification generateNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        builder.setContentTitle(mContent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setChannel(context);
            builder.setChannelId(CHANNEL_ID);
        }
        builder.setContentText("The task '" + mTaskName + "' just got triggered.");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.primaryLightColor));
        }
        return builder.build();
    }

    public static void setChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = manager.getNotificationChannel(CHANNEL_ID);
            if(channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
                channel.enableLights(true);
                channel.setLightColor(Color.MAGENTA);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }
        }
    }
}
