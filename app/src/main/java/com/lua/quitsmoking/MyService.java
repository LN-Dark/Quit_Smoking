package com.lua.quitsmoking;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.lua.quitsmoking.ui.home.AddSmoked;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class MyService extends Service {
    long startTime = 0;
    private Handler timerHandler = new Handler();
    private boolean isshown = false;
    private int smokedNumber = 0;

    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
            int intervaloTime = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent myIntent;
            PendingIntent pendingIntent;
            int intervaloTime_hour = prefs.getInt("Moon_QuitSmoking_Clock_hour", 0);
            int intervaloTime_minutes = prefs.getInt("Moon_QuitSmoking_Clock_minutes", 0);
            int dayClock = prefs.getInt("Moon_QuitSmoking_Clock_dayClock", 0);
            int monthClock = prefs.getInt("Moon_QuitSmoking_monthClock", 0);
            int yearClock = prefs.getInt("Moon_QuitSmoking_Clock_yearClock", 0);
            Calendar Datecompare = Calendar.getInstance();
            Datecompare.set(yearClock,monthClock,dayClock);
            Datecompare.set(Calendar.HOUR_OF_DAY, intervaloTime_hour);
            Datecompare.set(Calendar.MINUTE, intervaloTime_minutes);
            LocalDateTime ldt1 = LocalDateTime.of(yearClock, monthClock, dayClock, intervaloTime_hour, intervaloTime_minutes, 00);
            LocalDateTime ldt2 = LocalDateTime.of(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) +1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), 00);
            long d1 = Duration.between(ldt1, ldt2).toMinutes();
            myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,myIntent,0);
            if(d1 >= intervaloTime){
                if(!isshown){
                    manager.setAlarmClock(new AlarmManager.AlarmClockInfo(Calendar.getInstance().getTimeInMillis(), pendingIntent), pendingIntent);
                    isshown = true;
                    myTimer();
                }
            }else if((d1 >= 1 || isshown)){
                isshown = false;
            }
            int smokedtoday = prefs.getInt("Moon_QuitSmoking_SmokedToday", 0);
            if(smokedtoday != smokedNumber){
                ShowNotification();
            }
            timerHandler.postDelayed(timerRunnable, TimeUnit.MINUTES.toMillis(intervaloTime));
        }
    };

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ShowNotification();
        myTimer();
    }

    private void myTimer(){
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                startAlarm();
            }}, 0, 3000);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "102";
        String channelName = getString(R.string.servicoemexecucao);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public Notification notificationService;
    public NotificationCompat.Builder notificationBuilder;
    private void ShowNotification(){
        Intent addNewSmoked = new Intent(this, AddSmoked.class);
        addNewSmoked.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent addNewSmokedPendingIntent = PendingIntent.getBroadcast(this, 0, addNewSmoked, 0);
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, 0);
        SharedPreferences prefs = getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        smokedNumber = prefs.getInt("Moon_QuitSmoking_SmokedToday", 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.dontsmoke))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(openAppPendingIntent)
                .setSound(null)
                .setContentText(getString(R.string.youhavesmoked) + " " + smokedNumber + " " + getString(R.string.today))
                .addAction(R.drawable.quitsmoking, getString(R.string.more1), addNewSmokedPendingIntent);
        notificationService = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_smoke_free_black_24dp)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(2, notificationService);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ShowNotification();
        startAlarm();
        return START_STICKY;
    }

    private final IBinder mBinder = new LocalBinder();   // interface for clients that bind
    private boolean mAllowRebind;

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent(getApplicationContext(), DeviceBootReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    private void startAlarm() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        int intervaloTime = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
        if(intervaloTime != 0){
            startTime = System.currentTimeMillis();
            timerRunnable.run();
        }
    }
}

