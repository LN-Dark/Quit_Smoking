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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class MyService extends Service {
    long startTime = 0;

    private Handler timerHandler = new Handler();
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
            int monthClock = prefs.getInt("Moon_QuitSmoking_Clock_monthClock", 0);
            int yearClock = prefs.getInt("Moon_QuitSmoking_Clock_yearClock", 0);
            Calendar Datecompare = Calendar.getInstance();
            Datecompare.set(yearClock,monthClock,dayClock);
            Datecompare.set(Calendar.HOUR_OF_DAY, intervaloTime_hour);
            Datecompare.set(Calendar.MINUTE, intervaloTime_minutes);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date1 = null;
            Date date2 = null;
            try {
                String horasGravadas = intervaloTime_hour + ":" + intervaloTime_minutes;
                String horasAgora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
                date1 = simpleDateFormat.parse(horasGravadas);
                date2 = simpleDateFormat.parse(horasAgora);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long difference = date2.getTime() - date1.getTime();
            if(difference<0){
                difference=(date2.getTime() -date1.getTime() )+(date2.getTime()-date1.getTime());
            }
            int days = (int) (difference / (1000*60*60*24));
            int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,myIntent,0);
            if(min >= intervaloTime){
                manager.setAlarmClock(new AlarmManager.AlarmClockInfo(Calendar.getInstance().getTimeInMillis(), pendingIntent), pendingIntent);
            }
            timerHandler.postDelayed(this, TimeUnit.MINUTES.toMillis(intervaloTime));
        }
    };

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("102", getString(R.string.servicoemexecucao), NotificationManager.IMPORTANCE_HIGH);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_smoke_free_black_24dp)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
        startAlarm();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "102";
        String channelName = getString(R.string.servicoemexecucao);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        stopForeground(false);
    }

    private void startAlarm() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        int intervaloTime = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
        if(intervaloTime != 0){
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);timerHandler.postDelayed(timerRunnable, 0);
        }
    }


}
