package com.lua.quitsmoking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MyService.class);
        context.getApplicationContext().startForegroundService(serviceIntent );
    }
}
