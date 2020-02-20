package alonbd.simpler.BackgroundAndroid;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import alonbd.simpler.R;
import alonbd.simpler.TaskLogic.LocationTrigger;
import alonbd.simpler.TaskLogic.NotificationAction;
import alonbd.simpler.TaskLogic.Task;

public class LocationService extends Service {
    private static final String TAG = "ThugLocationService";
    private static final String ACTION_STOP_SERVICE = "actionStopService";

    private final static String CHANNEL_ID = "LocationServiceNotificationChannel";
    private final static CharSequence CHANNEL_NAME = "SimplerService";
    private final static int NOTIF_ID = -111;
    private final static int REQ_CODE = -50;
    final static int IMPORTANCE = NotificationManager.IMPORTANCE_LOW;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: location: " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                if(TasksManager.startAllWithLocation(LocationService.this, mLastLocation)) {
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIF_ID, generateNotification());
                }
            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback, Looper.getMainLooper()
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        startForeground(NOTIF_ID, generateNotification());
        if(intent.getAction() != null) {
            if(intent.getAction().equals(ACTION_STOP_SERVICE)) {
                Toast.makeText(this, "Restart the app to restart Location Service.", Toast.LENGTH_LONG).show();
                stopForeground(true);
                stopSelf();
            }
        }
        return Service.START_STICKY;
    }

    public Notification generateNotification() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = manager.getNotificationChannel(CHANNEL_ID);
            if(channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
                manager.createNotificationChannel(channel);
            }
        }

        String notificationContent = TasksManager.getInstance(this).getLocationTasksString();
        if(notificationContent == null) {
            notificationContent = "No more actions to look for.";
            stopForeground(true);
            stopSelf();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setChannelId(CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(getResources().getColor(R.color.primaryLightColor));
        }
        builder.setSubText("Location Service");
        builder.setContentTitle("Getting Location Updates...").setContentText("Looking for Tasks: " + notificationContent);
        //Action
        Intent stopIntent = new Intent(this, LocationService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPending = PendingIntent.getService(this, REQ_CODE, stopIntent, PendingIntent.FLAG_NO_CREATE);
        builder.addAction(android.R.drawable.ic_delete, "Stop", stopPending);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
}