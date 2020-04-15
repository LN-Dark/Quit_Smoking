package com.lua.quitsmoking;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lua.quitsmoking.ui.home.HomeFragment;
import com.lua.quitsmoking.ui.info.InfoFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        loadFragment(new HomeFragment());
        getSupportActionBar().setTitle(getString(R.string.app_name));
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setItemIconTintList(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(getDrawable(R.mipmap.ic_launcher));
        doBindService();
        ShowMinutesElapsed();
    }

    private boolean mShouldUnbind;
    public MyService mBoundService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MyService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    public void ShowMinutesElapsed(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
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
        Snackbar.make(findViewById(android.R.id.content),getString(R.string.japassaram) + " " + String.valueOf(min) + " " + getString(R.string.minutos), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void doBindService() {
        if (bindService(new Intent(MainActivity.this, MyService.class), mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e("Moon_Quit_Smoking", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    private void doUnbindService() {
        if (mShouldUnbind) {
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                getSupportActionBar().setTitle(getString(R.string.app_name));
                break;
            case R.id.navigation_info:
                fragment = new InfoFragment();
                getSupportActionBar().setTitle(getString(R.string.info));
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getFragments().isEmpty()){
            this.finish();
        }
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
