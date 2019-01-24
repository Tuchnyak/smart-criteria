package com.tuchnyak.smartcriteria;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectOverviewActivity extends AppCompatActivity {

    // fields for chart drawing
    private LineChart lineChart;
    private LineDataSet smartProjectMinPaceDataSet;
    private LineDataSet smartProjectMaxPaceDataSet;
    private LineDataSet smartProjectCurrentPaceDataSet;

    private TextView textViewProjectName;
    private ImageButton buttonTextInfo;
    private Vibrator vibrator;

    // field to operate with project that has been gotten
    private SmartProject smartProject;

    // field to store project id to load from project storage
    private int projectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_overview);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        projectId = intent.getIntExtra(MainActivity.PROJECT_ID_TRANSMIT_NAME, -1);

        if (projectId != -1) {

            smartProject = MainActivity.smartProjectList.get(projectId);

        } else {

            String msg = "PROBLEM DURING PROJECT UPLOADING!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e("=== ERR >>>", msg);

        }

        // line chart initialization
        lineChart = findViewById(R.id.lineChart);

        textViewProjectName = findViewById(R.id.textViewProjectName);
        textViewProjectName.setText(smartProject.getName());

        buttonTextInfo = findViewById(R.id.buttonTextInfo);

        // draw chart of initiated test project
        drawChart();
        setupChart();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }


    public void buttonIncreaseOnClick(View view) {

        smartProject.checkAndFillGaps();
        smartProject.increaseCurrentProgress();
        drawChart();
        setupChart();
        lineChart.notifyDataSetChanged();

        vibrator.vibrate(30);

        saveProject();

    }


    /**
     * Save project instance to shared preferences
     */
    private void saveProject() {

        MainActivity.adapter.notifyDataSetChanged();

    }


    public void showTextInfo(View view) {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.project_text_info_popup, null);

        DisplayMetrics dm = new DisplayMetrics();
        ProjectOverviewActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        final PopupWindow popupWindow = new PopupWindow(popupView, dm.widthPixels - 100, dm.heightPixels - 300);

        Button buttonDismiss = popupView.findViewById(R.id.buttonDismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        TextView textViewProjectInfo = popupView.findViewById(R.id.textViewProjectInfo);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        int percentage = (int) (smartProject.getCurrentProgress() * 100 / smartProject.getUnitsTotal());

        int progressToday = 0;

        if (smartProject.getEntriesCurrentPace().size() <= 1) {
            progressToday = (int) smartProject.getCurrentProgress();
        } else if (smartProject.getEntriesCurrentPace() != null) {
            Date[] keys = smartProject.getEntriesCurrentPace().keySet().toArray(new Date[0]);
            if (keys != null && keys.length >= 2)
                progressToday = (int) (smartProject.getEntriesCurrentPace().get(keys[keys.length - 2])
                        - smartProject.getEntriesCurrentPace().get(keys[keys.length - 1]));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(smartProject.getName()).append(" : ").append(Integer.valueOf(percentage)).append("%\n\n");
        sb.append("Description: ").append(smartProject.getDescription()).append("\n\n");
        sb.append("Today's progress: ").append(progressToday).append("\n");
        sb.append("Units done: ").append(smartProject.getCurrentProgress()).append("\n");
        sb.append("Units reamins: ").append(smartProject.getUnitsTotal() - smartProject.getCurrentProgress()).append("\n\n");
        sb.append("Minimum pace:").append("\n");
        sb.append("units per day: ").append(Math.round(smartProject.getUnitsPerDayMinPace())).append("\n");
        sb.append("deadline day: ").append(dateFormat.format(smartProject.getDeadlineDayMinPace())).append("\n\n");
        sb.append("Maximum pace:").append("\n");
        sb.append("units per day: ").append(Math.round(smartProject.getUnitsPerDayMaxPace())).append("\n");
        sb.append("deadline day: ").append(dateFormat.format(smartProject.getDeadlineDayMaxPace())).append("\n");

        textViewProjectInfo.setText(sb.toString());

        popupWindow.showAtLocation(lineChart, Gravity.CENTER, 0, 0);

    }


    /**
     * Setup chart options
     */
    private void setupChart() {

        float textSizeMinorChart = 12f;
        float textSizeMajorChart = 14f;
        float lineWidth = 3f;
        float circleRadius = 4f;
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

        lineChart.getLegend().setTextSize(16f);
        lineChart.setDescription(null);

        // on value selected
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                String xLabel = lineChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), lineChart.getXAxis());

                Toast.makeText(ProjectOverviewActivity.this,
                        xLabel + ": " + Math.round(e.getY()) + " units",
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }


    /**
     * Setup some dataset options
     *
     * @param dataSet    LineDataSet for setup
     * @param color      chart color
     * @param drawValues draw values of chart or not
     * @param textSize   values text size
     * @param lineWidth  width of an chart line
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
        lineChart.setMaxHighlightDistance(50f);
        lineChart.setData(chartData);
        lineChart.invalidate();

    }


    /**
     * Opening MainActivity instead of simple back to a saved instance
     * in order to update progress percentage
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        vibrator.cancel();

    }


}
