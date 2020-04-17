package com.lua.quitsmoking.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class AddSmoked extends BroadcastReceiver {
    private Context Ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        Ctx = context.getApplicationContext();
        addNewSmoked();
    }

    private void addNewSmoked(){
        SharedPreferences prefs = Ctx.getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        int smokedNumber = prefs.getInt("Moon_QuitSmoking_SmokedToday", 0);
        smokedNumber += 1;
        SharedPreferences.Editor editor = Ctx.getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE).edit();
        editor.putInt("Moon_QuitSmoking_SmokedToday", smokedNumber);
        editor.apply();
    }
}
