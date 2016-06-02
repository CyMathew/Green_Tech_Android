package app.greentech;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Cyril on 3/3/16.
 */

public class Fragment_Stats extends Fragment implements OnChartValueSelectedListener{

    private StatsDataSource dataSource = MainActivity.dataSource;

    private LineChart lineChart;
    private HorizontalBarChart barChart;

    private SimpleDateFormat dateFormat, dayMonthFormat;
    private Calendar cal;
    private String dayMonth, date;

    private ArrayList<Entry> lineEntries;
    private ArrayList<BarEntry> barEntries;
    private ArrayList<String> lineLabels, barLabels;

    private BarDataSet barDataset;
    private BarData barData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        lineChart = (LineChart) view.findViewById(R.id.chart_line);
        barChart = (HorizontalBarChart) view.findViewById(R.id.chart_bar);

        dataSource.open();

        dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
        dayMonthFormat = new SimpleDateFormat("MM-dd");

        setupLineChart();
        setupBarChart();

        lineChart.setOnChartValueSelectedListener(this);

        return view;

    }

    private void setupLineChart()
    {
        Entry e;
        lineEntries = new ArrayList<>();
        lineLabels = new ArrayList<String>();

        cal = Calendar.getInstance();

        LineDataSet dataset = new LineDataSet(lineEntries, "");
        LineData data = new LineData(lineLabels, dataset);

        Legend chartLegend = lineChart.getLegend();
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (new DecimalFormat("###,###,##0")).format(value);
            }
        });


        chartLegend.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGridColor(Color.GRAY);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(15f);

        yAxis.setAxisMinValue(0f);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(true);
        yAxis.setGridColor(Color.GRAY);
        yAxis.setTextColor(Color.GRAY);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDescription("");
        dataset.setColor(getResources().getColor(R.color.colorAccent));
        dataset.setFillColor(getResources().getColor(R.color.colorAccent));
        dataset.setDrawCircles(false);

        cal.add(Calendar.DATE, -7);

        for(int i = 0; i < 7; i++)
        {
            cal.add(Calendar.DATE, 1);
            date = dateFormat.format(cal.getTime());
            dayMonth = dayMonthFormat.format(cal.getTime());
            //Log.i("VALUE", "Date is: " + dayMonth);

            lineLabels.add(dayMonth);
            e = new Entry((float)dataSource.getTotal(date), i);
            Log.i("TOTAL", String.valueOf(dataSource.getTotal(date)));
            e.setData(date);
            lineEntries.add(e);
        }
        lineChart.setData(data);

        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        dataset.setDrawValues(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setScaleEnabled(false);

        lineChart.notifyDataSetChanged();
        lineChart.animateY(2500);
    }

    private void setupBarChart()
    {
        barEntries = new ArrayList<>();
        barLabels = new ArrayList<String>();

        barDataset = new BarDataSet(barEntries, "");
        barData = new BarData(barLabels, barDataset);
        barChart.setData(barData);

        barLabels.add("Glass");
        barLabels.add("Aluminum");
        barLabels.add("Plastic");
        barLabels.add("Paper");

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        date = dateFormat.format(cal.getTime());

        Legend chartLegend = barChart.getLegend();
        XAxis xAxis = barChart.getXAxis();
        YAxis yAxis = barChart.getAxisLeft();

        chartLegend.setEnabled(false);
        xAxis.setGridColor(Color.GRAY);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        yAxis.setAxisMinValue(0f);
        yAxis.setAxisMaxValue(10f);
        yAxis.setGridColor(Color.GRAY);
        yAxis.setTextColor(Color.GRAY);
        yAxis.setDrawZeroLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setDescription("");
        barChart.setScaleEnabled(false);

        barDataset.setColors(ColorTemplate.LIBERTY_COLORS);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (new DecimalFormat("###,###,##0")).format(value);
            }
        });

        barDataset.setBarSpacePercent(55f);


        barChart.setAutoScaleMinMaxEnabled(true);
        cal.add(Calendar.DATE, -1);
        date = dateFormat.format(cal.getTime());
        Log.i("DATE", date);
        updateChartForDate(date);
    }

    private void updateChartForDate(String date)
    {
        barEntries.clear();
        barChart.notifyDataSetChanged();
        barChart.animateY(2500);

        barEntries.add(new BarEntry(dataSource.getAmount("Paper", date), 3));
        Log.i("VALUE", "Paper: "+ dataSource.getAmount("Paper", date));
        barEntries.add(new BarEntry(dataSource.getAmount("Plastic", date), 2));
        barEntries.add(new BarEntry(dataSource.getAmount("Aluminum", date), 1));
        barEntries.add(new BarEntry(dataSource.getAmount("Glass", date), 0));

        barChart.notifyDataSetChanged();
        barChart.animateY(3500);

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        //Log.i("VALUE", String.valueOf(e.getVal()) + " " + e.getData().toString());
        updateChartForDate(e.getData().toString());
    }

    @Override
    public void onNothingSelected() {

    }
}