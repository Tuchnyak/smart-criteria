package com.tuchnyak.smartcriteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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


    }


    public void buttonIncreaseOnClick(View view) {

        smartProject.checkAndFillGaps();
        smartProject.increaseCurrentProgress();
        drawChart();
        setupChart();
        lineChart.notifyDataSetChanged();

    }


    /**
     * Setup chart options
     */
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


    /**
     * Setup some dataset options
     * @param dataSet
     * @param color
     * @param drawValues
     * @param textSize
     * @param lineWidth
     */
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


    /**
     * Draw chart from project instance
     */
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
        XAxis xAxis = lineChart.getXAxis();

        final String[] days = smartProject.getDaysAsStringArray();

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

        xAxis.setValueFormatter(xAxisFormatter);

        // setup chart data and draw
        lineChart.setData(chartData);
        lineChart.invalidate();

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

    }


}
