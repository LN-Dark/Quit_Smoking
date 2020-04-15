package com.lua.quitsmoking.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.lua.quitsmoking.MainActivity;
import com.lua.quitsmoking.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MaterialButton btn_gravar = root.findViewById(R.id.btn_gravar_intervalo);
        MaterialButton btn_ShowTime = root.findViewById(R.id.btn_timeElapsed);
        TextInputEditText edtxt_intervalo = root.findViewById(R.id.edtxt_intervalodetempo);
        SharedPreferences prefs = root.getContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        btn_ShowTime.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.ShowMinutesElapsed();
        });
        int intervaloTime = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
        if(intervaloTime != 0){
            edtxt_intervalo.setText(String.valueOf(intervaloTime));
        }
        btn_gravar.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            SharedPreferences.Editor editor = root.getContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE).edit();
            editor.putInt("Moon_QuitSmoking_Clock_interval", Integer.parseInt(edtxt_intervalo.getText().toString()));
            editor.putInt("Moon_QuitSmoking_Clock_hour", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            editor.putInt("Moon_QuitSmoking_Clock_minutes", Calendar.getInstance().get(Calendar.MINUTE));
            editor.putInt("Moon_QuitSmoking_Clock_dayClock", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            editor.putInt("Moon_QuitSmoking_monthClock", Calendar.getInstance().get(Calendar.MONTH));
            editor.putInt("Moon_QuitSmoking_Clock_yearClock", Calendar.getInstance().get(Calendar.YEAR));
            editor.apply();
            mainActivity.mBoundService.timerRunnable.run();
            Toast.makeText(root.getContext(), getString(R.string.intervaloguardado), Toast.LENGTH_LONG).show();
        });
        return root;
    }
}
