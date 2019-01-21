package com.tuchnyak.smartcriteria;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tuchnyak.smartcriteria.entity.SmartProject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProjectFormActivity extends AppCompatActivity {

    private EditText editTextProjectName;
    private EditText editTextUnitsAmount;
    private EditText editTextProjectDescription;

    private EditText editTextStartDay;
    private ImageButton buttonStartDayPicker;

    private RadioGroup radioGroupDeadlinePace;
    private RadioButton radioButtonDeadline;
    private RadioButton radioButtonPace;

    private LinearLayout verticalLayoutDeadline;
    private LinearLayout verticalLayoutPace;

    private EditText editTextDeadlineMinPace;
    private ImageButton buttonDeadlineMinPaceDatePicker;

    private EditText editTextDeadlineMaxPace;
    private ImageButton buttonDeadlineMaxPaceDatePicker;

    private EditText editTextUnitsPerDayMinPace;
    private EditText editTextUnitsPerDayMaxPace;

    private Button buttonCreateProject;

    private boolean isPaceMode = false;
    private String command;

    private Date startDay;
    private Date deadlineMinPaceDate;
    private Date deadlineMaxPaceDate;

    private SmartProject formProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_form);

        initiateUI();

        command = getIntent().getStringExtra(MainActivity.COMMAND_TRANSMIT_NAME);

        if (command.equals(MainActivity.COMMAND_EDIT)) {
            // TODO populate fields
        } else {
            // do nothing
        }

    }


    public void createNewProject(View view) {

        if (command.equals(MainActivity.COMMAND_CREATE)) {

            String name = editTextProjectName.getText().toString();
            String description = editTextProjectDescription.getText().toString();
            int unitsTotal = Integer.parseInt(editTextUnitsAmount.getText().toString());

            if (isPaceMode) {

                double unitsPerDayMinPace = Double.parseDouble(editTextUnitsPerDayMinPace.getText().toString());
                double unitsPerDayMaxPace = Double.parseDouble(editTextUnitsPerDayMaxPace.getText().toString());
                formProject = new SmartProject(name, description, unitsTotal, startDay, unitsPerDayMinPace, unitsPerDayMaxPace);

            } else {

                formProject = new SmartProject(name, description, unitsTotal, startDay, deadlineMinPaceDate, deadlineMaxPaceDate);

            }

            Intent intent = new Intent(getApplicationContext(), ProjectOverviewActivity.class);
            intent.putExtra(MainActivity.PROJECT_ID_TRANSMIT_NAME, MainActivity.smartProjectList.size());

            MainActivity.smartProjectList.add(formProject);
            MainActivity.adapter.notifyDataSetChanged();

            startActivity(intent);

        } else {
            // TODO CREATE MODE
        }

    }


    public void pickDate(View view) {

        final String tag = view.getTag().toString();

        Calendar c = Calendar.getInstance();

        int cDay = c.get(Calendar.DAY_OF_MONTH);
        int cMonth = c.get(Calendar.MONTH);
        int cYear = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProjectFormActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());

                        switch (tag) {
                            case "date_picker_start":
                                startDay = calendar.getTime();
                                editTextStartDay.setText(dateFormat.format(startDay));
                                break;

                            case "date_picker_min":
                                deadlineMinPaceDate = calendar.getTime();
                                editTextDeadlineMinPace.setText(dateFormat.format(deadlineMinPaceDate));
                                break;

                            case "date_picker_max":
                                deadlineMaxPaceDate = calendar.getTime();
                                editTextDeadlineMaxPace.setText(dateFormat.format(deadlineMaxPaceDate));
                                break;
                        }


                    }
                }, cYear, cMonth, cDay);

        datePickerDialog.show();

    }


    private void initiateUI() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextProjectName = findViewById(R.id.editTextProjectName);
        editTextUnitsAmount = findViewById(R.id.editTextUnitsAmount);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);

        editTextStartDay = findViewById(R.id.editTextStartDay);
        buttonStartDayPicker = findViewById(R.id.buttonStartDayPicker);

        radioGroupDeadlinePace = findViewById(R.id.radioGroupDeadlinePace);
        radioButtonDeadline = findViewById(R.id.radioButtonDeadline);
        radioButtonPace = findViewById(R.id.radioButtonPace);

        verticalLayoutDeadline = findViewById(R.id.verticalLayoutDeadline);
        verticalLayoutPace = findViewById(R.id.verticalLayoutPace);
        verticalLayoutPace.setVisibility(View.GONE);

        editTextDeadlineMinPace = findViewById(R.id.editTextDeadlineMinPace);
        buttonDeadlineMinPaceDatePicker = findViewById(R.id.buttonDeadlineMinPaceDatePicker);

        editTextDeadlineMaxPace = findViewById(R.id.editTextDeadlineMaxPace);
        buttonDeadlineMaxPaceDatePicker = findViewById(R.id.buttonDeadlineMaxPaceDatePicker);

        editTextUnitsPerDayMinPace = findViewById(R.id.editTextUnitsPerDayMinPace);
        editTextUnitsPerDayMaxPace = findViewById(R.id.editTextUnitsPerDayMaxPace);

        buttonCreateProject = findViewById(R.id.buttonCreateProject);

    }


    public void radioClick(View view) {

        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (view.getTag().equals("radio_dead")) {

            isPaceMode = false;

            verticalLayoutDeadline.setVisibility(View.VISIBLE);
            verticalLayoutPace.setVisibility(View.GONE);

            params.addRule(RelativeLayout.BELOW, R.id.verticalLayoutDeadline);
            buttonCreateProject.setLayoutParams(params);

        } else if (view.getTag().equals("radio_pace")) {

            isPaceMode = true;

            verticalLayoutDeadline.setVisibility(View.GONE);
            verticalLayoutPace.setVisibility(View.VISIBLE);

            params.addRule(RelativeLayout.BELOW, R.id.verticalLayoutPace);
            buttonCreateProject.setLayoutParams(params);

        } else {

            String msg = "Something gone wrong with radio buttons";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            Log.e("=== ERR >>>", msg);

        }

    }


}
