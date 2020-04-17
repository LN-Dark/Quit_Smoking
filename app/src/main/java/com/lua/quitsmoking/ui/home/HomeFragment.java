package com.lua.quitsmoking.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lua.quitsmoking.MainActivity;
import com.lua.quitsmoking.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        MaterialButton btn_gravar = root.findViewById(R.id.btn_gravar_intervalo);
        MaterialButton btn_ShowTime = root.findViewById(R.id.btn_timeElapsed);
        TextInputEditText edtxt_intervalo = root.findViewById(R.id.edtxt_intervalodetempo);
        TextInputEditText edtxt_incremento = root.findViewById(R.id.edtxt_tempoIncrease);
        SharedPreferences prefs = root.getContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        btn_ShowTime.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.ShowMinutesElapsed();
        });


        int intervaloTime = prefs.getInt("Moon_QuitSmoking_Clock_interval", 0);
        int incrementoTime = prefs.getInt("Moon_QuitSmoking_Clock_incremento", 0);
        int hourConfirm = prefs.getInt("Moon_QuitSmoking_Clock_hour", 0);
        if(intervaloTime != 0){
            edtxt_intervalo.setText(String.valueOf(intervaloTime));
            edtxt_incremento.setText(String.valueOf(incrementoTime));
        }
        btn_gravar.setOnClickListener(v -> {
            if(!edtxt_intervalo.getText().toString().equals("")){
                if(!edtxt_incremento.getText().toString().equals("")){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    SharedPreferences.Editor editor = root.getContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE).edit();
                    editor.putInt("Moon_QuitSmoking_Clock_interval", Integer.parseInt(edtxt_intervalo.getText().toString()));
                    editor.putInt("Moon_QuitSmoking_Clock_incremento", Integer.parseInt(edtxt_incremento.getText().toString()));
                    if(hourConfirm == 0){
                        editor.putInt("Moon_QuitSmoking_Clock_hour", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                        editor.putInt("Moon_QuitSmoking_Clock_minutes", Calendar.getInstance().get(Calendar.MINUTE));
                        editor.putInt("Moon_QuitSmoking_Clock_dayClock", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        editor.putInt("Moon_QuitSmoking_monthClock", Calendar.getInstance().get(Calendar.MONTH) +1);
                        editor.putInt("Moon_QuitSmoking_Clock_yearClock", Calendar.getInstance().get(Calendar.YEAR));
                    }
                    editor.apply();
                    mainActivity.mBoundService.timerRunnable.run();
                    Snackbar.make(getActivity().findViewById(android.R.id.content),getString(R.string.intervaloguardado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.preencheotempoaincrementar), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.preencheointervalodetempo), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        SetChart();
        return root;
    }

    private void SetChart(){
        LineChartView lineChartView;
        lineChartView = root.findViewById(R.id.chart);

        SharedPreferences prefs = root.getContext().getSharedPreferences("Moon_QuitSmoking_Clock", MODE_PRIVATE);
        Set<String> sets = new HashSet<>();
        sets = prefs.getStringSet("Moon_QuitSmoking_Chart_Smoked",new HashSet<String>());
        if(!sets.isEmpty()){
            ArrayList<String> datas = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();

            for(String set : sets){
                String[] valorset = set.split("-");
                datas.add(valorset[0]);
                valores.add(valorset[1]);
            }
            Collections.reverse(datas);
            Collections.reverse(valores);
            List<PointValue> yAxisValues = new ArrayList<PointValue>();
            List<AxisValue> axisValues = new ArrayList<AxisValue>();


            Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

            for (int i = 0; i < datas.size(); i++) {
                axisValues.add(i, new AxisValue(i).setLabel(datas.get(i)));
            }

            for (int i = 0; i < valores.size(); i++) {
                yAxisValues.add(new PointValue(i, Integer.parseInt(valores.get(i))));
            }

            List<Line> lines = new ArrayList<>();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(13);
            axis.setName(getString(R.string.datas));
            axis.setTextColor(Color.parseColor("#03A9F4"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
            yAxis.setName(getString(R.string.fumados));
            yAxis.setTextColor(Color.parseColor("#03A9F4"));
            yAxis.setTextSize(14);
            data.setAxisYLeft(yAxis);

            lineChartView.setLineChartData(data);
            lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.nodia) + " " + datas.get(pointIndex) + " " + getString(R.string.fumaste) + " " + valores.get(pointIndex), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                @Override
                public void onValueDeselected() {

                }
            });
            Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.top = 40;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }

    }
}
