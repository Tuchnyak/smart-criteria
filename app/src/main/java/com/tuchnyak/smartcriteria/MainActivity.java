package com.tuchnyak.smartcriteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tuchnyak.smartcriteria.entity.SmartProject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;
    private LineDataSet smartProjectMinPaceDataSet;
    private LineDataSet smartProjectMaxPaceDataSet;
    private LineDataSet smartProjectCurrentPaceDataSet;

    private IAxisValueFormatter xAxisFormatter;

    private SmartProject smartProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.lineChart);

        // initiate test project
        initiateProject();

        // draw chart of initiated test project
        drawChart();
        setupChart();

        // simulate increasing of a project's progress


        // **********************************************
        //
        // generate fake current map for a project
//        Map<Date, Float> fakeCurrentData = new TreeMap<>();
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime()), 253f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 1 * 24 * 60 * 60 * 1000), 247f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 2 * 24 * 60 * 60 * 1000), 234f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 3 * 24 * 60 * 60 * 1000), 234f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 4 * 24 * 60 * 60 * 1000), 217f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 5 * 24 * 60 * 60 * 1000), 206f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 6 * 24 * 60 * 60 * 1000), 201f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 7 * 24 * 60 * 60 * 1000), 188f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 8 * 24 * 60 * 60 * 1000), 169f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 9 * 24 * 60 * 60 * 1000), 154f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 10 * 24 * 60 * 60 * 1000), 143f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 11 * 24 * 60 * 60 * 1000), 140f);
//        fakeCurrentData.put(new Date(smartProject.getStartDay().getTime() + 12 * 24 * 60 * 60 * 1000), 132f);
//
//        smartProject.setEntriesCurrentPace(fakeCurrentData);
//        smartProject.setCurrentProgress(272 - 132);
//
//        smartProject.checkAndFillGaps();
//
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//        smartProject.increaseCurrentProgress();
//
//        drawChart();
//        setupChart();

        smartProject.increaseCurrentProgress();
        drawChart();
        setupChart();
        smartProject.increaseCurrentProgress();
        drawChart();
        setupChart();
        smartProject.increaseCurrentProgress();
        drawChart();
        setupChart();

    }


    private void setupChart() {

        float textSizeMinorChart = 12f;
        float textSizeMajorChart = 14f;
        float textSizeDescription = 20f;
        float lineWidth = 3f;
        float circleRadius = 4f;
        float descriptionXOffset = 150f;
        float descriptionYOffset = 70f;
        int fillAlpha = 25;
        boolean showGridLines = false;

        if (smartProjectMinPaceDataSet != null) {
            dataSetSetup(smartProjectMinPaceDataSet, R.color.colorMinPace, false, textSizeMinorChart, lineWidth);
        }

        if (smartProjectMaxPaceDataSet != null) {
            dataSetSetup(smartProjectMaxPaceDataSet, R.color.colorMaxPace, false, textSizeMinorChart, lineWidth);
        }

        if (smartProjectCurrentPaceDataSet != null) {
            dataSetSetup(smartProjectCurrentPaceDataSet, R.color.colorCurrentPace, true, textSizeMajorChart, lineWidth);
            smartProjectCurrentPaceDataSet.setCircleRadius(circleRadius); //
            smartProjectCurrentPaceDataSet.setDrawFilled(true); //
            smartProjectCurrentPaceDataSet.setFillColor(getResources().getColor(R.color.colorCurrentPace)); //
            smartProjectCurrentPaceDataSet.setFillAlpha(fillAlpha); //
        }

        // setup X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawGridLines(showGridLines);

        // setup Y axis
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawGridLines(showGridLines);
        yAxisLeft.setDrawGridLinesBehindData(showGridLines);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawGridLines(showGridLines);

        //*** setup chart description
        Description chartDescription = new Description();
        chartDescription.setText(smartProject.getName());
        chartDescription.setTextSize(textSizeDescription);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        chartDescription.setPosition(dm.widthPixels - descriptionXOffset, descriptionYOffset);
        lineChart.setDescription(chartDescription);
        //***

        // on value selected
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                String xLabel = lineChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), lineChart.getXAxis());

                Toast.makeText(MainActivity.this,
                        "Remainder at the end of " + xLabel + ": " + Math.round(e.getY()),
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }


    private void dataSetSetup(LineDataSet dataSet, int color, boolean drawValues, float textSize, float lineWidth) {
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(getResources().getColor(color));
        dataSet.setDrawCircleHole(false);
        dataSet.setCircleColor(getResources().getColor(color));
        dataSet.setLineWidth(lineWidth);
        dataSet.setDrawValues(drawValues);
        dataSet.setValueTextColor(getResources().getColor(color));
        dataSet.setValueTextSize(textSize);
    }


    private void drawChart() {

        smartProject.checkAndFillGaps();

        List<Entry> entriesMinPace = new ArrayList<>();
        List<Entry> entriesMaxPace = new ArrayList<>();
        List<Entry> entriesCurrentPace = new ArrayList<>();

        // get float values for X axis
        float[] xAxisNumbers = smartProject.getFloatNumbersForXAxis();

        // populating of min pace list of entries
        float[] yAxisMinPaceValues = smartProject.getYAxisChartValuesMinPace();
        for (int i = 0; i < yAxisMinPaceValues.length; i++) {
            entriesMinPace.add(new Entry(xAxisNumbers[i], yAxisMinPaceValues[i]));
        }

        // populating of max pace list of entries
        float[] yAxisMaxPaceValues = smartProject.getYAxisChartValuesMaxPace();
        for (int i = 0; i < yAxisMaxPaceValues.length; i++) {
            entriesMaxPace.add(new Entry(xAxisNumbers[i], yAxisMaxPaceValues[i]));
        }

        // populating of current pace list of entries
        float[] yAxisCurrentPaceValues = smartProject.getYAxisChartValuesCurrentPace();
        for (int i = 0; i < yAxisCurrentPaceValues.length; i++) {
            entriesCurrentPace.add(new Entry(xAxisNumbers[i], yAxisCurrentPaceValues[i]));
        }


        // creating LineDataSet for both paces
        smartProjectMinPaceDataSet = new LineDataSet(entriesMinPace, "min pace");
        smartProjectMaxPaceDataSet = new LineDataSet(entriesMaxPace, "max pace");
        // setup line data set for current progress
        smartProjectCurrentPaceDataSet = new LineDataSet(entriesCurrentPace, "current progress");

        // creating a list of IDataSets
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(smartProjectMinPaceDataSet);
        dataSets.add(smartProjectMaxPaceDataSet);
        dataSets.add(smartProjectCurrentPaceDataSet);

        // creating line chart data
        LineData chartData = new LineData(dataSets);

        // setup labels for X axis
        final String[] days = smartProject.getDaysAsStringArray();
        xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };


        // setup chart data and draw
        lineChart.setData(chartData);
        lineChart.invalidate();


//        // get data and datasets from line chart
//        LineData reverseChartData = lineChart.getData();
//        List<ILineDataSet> reverseDataSets = reverseChartData.getDataSets();
//
//        // add current data set to existing data sets
//        reverseDataSets.add(smartProjectCurrentPaceDataSet);

        // draw chart
//        lineChart.setData(reverseChartData);
//        lineChart.invalidate();

    }


    private void initiateProject() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date startDay = null;
        Date deadlineMinPace = null;
        Date deadlineMaxPace = null;

        try {
            startDay = dateFormat.parse("26-11-2018");
            deadlineMinPace = dateFormat.parse("23-12-2018");
            deadlineMaxPace = dateFormat.parse("09-12-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDay != null && deadlineMinPace != null && deadlineMaxPace != null) {

            smartProject = new SmartProject("Test project", "Test project description",
                    272, startDay, deadlineMinPace, deadlineMaxPace);

            Log.i("===>>>", smartProject.toString());
            smartProject.printEntries();

        } else {
            Log.e("===>>>", "Some date are null!");
        }

        for (float f : smartProject.getYAxisChartValuesMinPace()) {
            System.out.println(f);
        }

        for (float f : smartProject.getYAxisChartValuesMaxPace()) {
            System.out.println(f);
        }

        for (float f : smartProject.getFloatNumbersForXAxis()) {
            System.out.println(f);
        }


        /**
         //         * Testing Project 2
         //         */
//
//        startDay = null;
//
//        try {
//            startDay = dateFormat.parse("01-11-2018");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        SmartProject smartProject2 = null;
//
//        if (startDay != null) {
//
//            smartProject2 = new SmartProject("Test project 2", "Second test descroption",
//                    53, startDay, 7, 13);
//
//            Log.i("===>>>", smartProject2.toString());
//            smartProject2.printEntries();
//
//        } else {
//            Log.e("===>>>", "Some date are null!");
//        }
//
//        for (float f : smartProject2.getYAxisChartValuesMinPace()) {
//            System.out.println(f);
//        }
//        for (float f : smartProject2.getYAxisChartValuesMaxPace()) {
//            System.out.println(f);
//        }
//        for (float f : smartProject2.getFloatNumbersForXAxis()) {
//            System.out.println(f);
//        }
//
//        /**
//         *******************************************************************************************
//         */

    }


}
