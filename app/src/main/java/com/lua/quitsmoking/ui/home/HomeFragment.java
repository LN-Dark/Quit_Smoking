package com.lua.quitsmoking.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import java.util.Map;
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
        BarChart barChart = root.findViewById(R.id.barchart);
        SharedPreferences prefsSmoked = root.getContext().getSharedPreferences("Moon_QuitSmoking_Chart_Smoked", MODE_PRIVATE);
        Map<String, ?> allEntries = prefsSmoked.getAll();
        if(!allEntries.isEmpty()){
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();
            ArrayList<String> posicaoOrdem = new ArrayList<>();
            ArrayList<String> valoresOrdem = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                posicaoOrdem.add(entry.getKey());
                valoresOrdem.add(entry.getValue().toString());
            }
            for(int i = 0 ; i < posicaoOrdem.size(); i++){
                String[] datatDecomp = valoresOrdem.get(posicaoOrdem.indexOf(String.valueOf(i +1))).split("-");
                labels.add(datatDecomp[0]);
                valores.add(datatDecomp[1]);
            }
            List<BarEntry> yAxisValues = new ArrayList<>();
            for (int i = 0; i < valores.size(); i++) {
                yAxisValues.add(new BarEntry(Integer.parseInt(valores.get(i)), i));
            }
            BarDataSet bardataset = new BarDataSet(yAxisValues, "Dias");
            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            bardataset.setValueTextColor(root.getContext().getColor(R.color.colorBorder));
            barChart.animateY(2000);
            barChart.setDescription(" ");

            Legend legend = barChart.getLegend();
            legend.setTextColor(root.getContext().getColor(R.color.colorBorder));
            XAxis xAxis = barChart.getXAxis();
            xAxis.setTextColor(root.getContext().getColor(R.color.colorBorder));
            YAxis yAxisleft = barChart.getAxisLeft();
            bardataset.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + ((int) value);
                }
            });
            yAxisleft.setTextColor(root.getContext().getColor(R.color.colorBorder));
            YAxis yAxisright = barChart.getAxisRight();
            yAxisright.setTextColor(root.getContext().getColor(R.color.colorBorder));

            barChart.animateXY(3000, 3000);
            BarData data = new BarData(labels, bardataset);
            data.setValueTextColor(root.getContext().getColor(R.color.colorBorder));
            barChart.setData(data);


        }



    }

}
