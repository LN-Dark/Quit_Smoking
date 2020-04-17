package com.lua.quitsmoking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reqCode = 1;
        SharedPreferences prefs = context.getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class);
        showNotification(context, context.getString(R.string.fumar), context.getString(R.string.japodesirfumar), intent1, reqCode);
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notificacaofumar);
            String description = context.getString(R.string.notificacaoparafumar);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Moon_QuitSmoking_channel", name, importance);
            channel.setDescription(description);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.enableLights(true);
            channel.setSound(alarmSound, audioAttributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "Moon_QuitSmoking_channel")
                .setSmallIcon(R.drawable.ic_smoke_free_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 500, 1000})
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(reqCode, builder.build());
        SharedPreferences prefs = context.getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        int diaCount = prefs.getInt("Moon_QuitSmoking_Clock_dayClock", 0);
        SharedPreferences.Editor editor = context.getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE).edit();
        if(diaCount != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            int timeInterval = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
            int timeIncremento = prefs.getInt("Moon_QuitSmoking_Clock_incremento", 0);
            int smokedtoday = prefs.getInt("Moon_QuitSmoking_SmokedToday", 0);
            timeInterval += timeIncremento;
            Set<String> sets = new HashSet<>();
            sets = prefs.getStringSet("Moon_QuitSmoking_Chart_Smoked",new HashSet<String>());
            sets.add(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1)
                    + "-" + smokedtoday);
            editor.putStringSet("Moon_QuitSmoking_Chart_Smoked",sets);
            editor.putInt("Moon_QuitSmoking_Clock_interval", timeInterval);
            editor.putInt("Moon_QuitSmoking_SmokedToday", 0);
        }
        editor.putInt("Moon_QuitSmoking_Clock_hour", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        editor.putInt("Moon_QuitSmoking_Clock_minutes", Calendar.getInstance().get(Calendar.MINUTE));
        editor.putInt("Moon_QuitSmoking_Clock_dayClock", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        editor.putInt("Moon_QuitSmoking_monthClock", Calendar.getInstance().get(Calendar.MONTH) + 1);
        editor.putInt("Moon_QuitSmoking_Clock_yearClock", Calendar.getInstance().get(Calendar.YEAR));
        editor.apply();
        Intent serviceIntent = new Intent(context.getApplicationContext(), MyService.class);
        context.startForegroundService(serviceIntent );
    }
}
