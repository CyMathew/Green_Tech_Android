package app.greentech;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Random;


/**
 * Fragment loaded into MainActivity that shows charts of all recycling done during the week.
 * Uses MPAndroidChart
 * @author Cyril Mathew on 3/3/16.
 */

public class Fragment_Stats extends Fragment implements OnChartValueSelectedListener{

    /**
     * DataSource reference for MainActivity's copy of the database reference
     */
    private StatsDataSource dataSource = MainActivity.dataSource;

    /**
     * LineChart declaration
     */
    private LineChart lineChart;

    /**
     * HorizontalBarChart declaration
     */
    private HorizontalBarChart barChart;

    /**
     * SimpleDateFormat declaration used to show dates within charts
     */
    private SimpleDateFormat dateFormat, dayMonthFormat;

    /**
     * Calendar object declaration for use with SimpleDateFormat objects.
     */
    private Calendar cal;

    /**
     * Strings used to store date values
     */
    private String dayMonth, date;

    /**
     * ArrayList used to store values for LineChart
     */
    private ArrayList<Entry> lineEntries;

    /**
     * ArrayList used to store values for BarChart
     */
    private ArrayList<BarEntry> barEntries;

    /**
     * ArrayLists used to store the labels for LineChart and BarChart
     */
    private ArrayList<String> lineLabels, barLabels;

    /**
     * Used to manipulate bar chart values as a group
     */
    private BarDataSet barDataset;

    /**
     * Used to manipulate bar chart datasets as a group
     */
    private BarData barData;

    SharedPreferences preferences;

    private static final String demoPref_active = "demoStats";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        lineChart = (LineChart) view.findViewById(R.id.chart_line);
        barChart = (HorizontalBarChart) view.findViewById(R.id.chart_bar);

        //Open connection to database
        dataSource.open();

        dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
        dayMonthFormat = new SimpleDateFormat("MM-dd");

        //Get Shared Preferences to see if Stats Demo data should be off or on
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i("Pref", String.valueOf(preferences.getBoolean("init", false)));

        //If Stats Demo data is on
        if(!preferences.getBoolean("init", false))
        {
            //add random values to the 7 days of the week
            SharedPreferences.Editor prefEdit = preferences.edit();
            prefEdit.putBoolean("init", true);
            prefEdit.putBoolean(demoPref_active, true);
            Log.i("Pref", String.valueOf(preferences.getBoolean("init", false)));
            prefEdit.commit();
            demoSetup();
        }

        //Initialize LineChart for viewing
        setupLineChart();

        //Setup listener for when user selects a specific date within the LineChart
        lineChart.setOnChartValueSelectedListener(this);

        //Initialize BarChart for viewing
        setupBarChart();


        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stats_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.stats_menu_option:

                Boolean val = !(preferences.getBoolean(demoPref_active, false));

                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putBoolean(demoPref_active, val);
                prefEdit.commit();

                return true;

            default:
                break;
        }

        return false;
    }

    /**
     * Initial set up of the main line chart
     */
    private void setupLineChart()
    {
        //Declaration of object that stores a single entry
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


        //Set up line chart's visuals
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
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        dataset.setDrawValues(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setScaleEnabled(false);

        //Get the date of the day 7 days ago
        cal.add(Calendar.DATE, -7);

        //Calculate the dates of the entire week
        //Add it to the line labels
        for(int i = 0; i < 7; i++)
        {
            cal.add(Calendar.DATE, 1);
            date = dateFormat.format(cal.getTime());
            dayMonth = dayMonthFormat.format(cal.getTime());

            lineLabels.add(dayMonth);
            e = new Entry((float)dataSource.getTotal(date), i);
            e.setData(date);
            lineEntries.add(e);
        }

        //Add dates to the line chart and animate
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.animateY(2500);

    }

    /**
     * Setup of the initial bar chart at the bottom showing itemized recycling during specified day
     */
    private void setupBarChart()
    {
        //Declare bar entries and labels
        barEntries = new ArrayList<>();
        barLabels = new ArrayList<String>();

        //Declare bar data, datasets and set initial data
        barDataset = new BarDataSet(barEntries, "");
        barData = new BarData(barLabels, barDataset);
        barChart.setData(barData);

        //Add bar labels
        barLabels.add("Glass");
        barLabels.add("Aluminum");
        barLabels.add("Plastic");
        barLabels.add("Paper");

        //Get/Set today's date
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        date = dateFormat.format(cal.getTime());

        //Setup barchart
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


        updateChartForDate(date);
    }

    /**
     * Setups the charts for demo values to be shown
     */
    private void demoSetup()
    {
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        Random r = new Random();

        for(int i = 0; i < 7; i++)
        {
            cal.add(Calendar.DATE, 1);
            date = dateFormat.format(cal.getTime());

            dataSource.addToStats("Paper", date, r.nextInt(3 - 0));
            dataSource.addToStats("Glass", date, r.nextInt(3 - 0));
            dataSource.addToStats("Aluminum", date, r.nextInt(3 - 0));
            dataSource.addToStats("Plastic", date, r.nextInt(3 - 0));
        }

    }

    /**
     * Update bar chart for the date picked by the user
     * @param date
     */
    private void updateChartForDate(String date)
    {
        barEntries.clear();
        barChart.notifyDataSetChanged();
        barChart.animateY(2500);

        barEntries.add(new BarEntry(dataSource.getAmount("Paper", date), 3));
        barEntries.add(new BarEntry(dataSource.getAmount("Plastic", date), 2));
        barEntries.add(new BarEntry(dataSource.getAmount("Aluminum", date), 1));
        barEntries.add(new BarEntry(dataSource.getAmount("Glass", date), 0));

        barChart.notifyDataSetChanged();
        barChart.animateY(3500);

    }

    /**
     * Accepts input from user when a value is selected on the line chart
     * @param e
     * @param dataSetIndex
     * @param h
     */
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        updateChartForDate(e.getData().toString());
    }

    @Override
    public void onNothingSelected() {}
}