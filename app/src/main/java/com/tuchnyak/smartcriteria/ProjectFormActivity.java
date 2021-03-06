package com.tuchnyak.smartcriteria;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

    // Set of input areas for name, total units amount and description
    private EditText editTextProjectName;
    private EditText editTextUnitsTotalAmount;
    private EditText editTextProjectDescription;

    // Set of button and view to pick and show start day value
    private EditText editTextStartDay;
    private ImageButton buttonStartDayPicker;

    // Radio buttons group to choose creation mode: by dates or by pace
    private RadioGroup radioGroupDeadlinePace;
    private RadioButton radioButtonDeadline;
    private RadioButton radioButtonPace;

    // Layouts to organize input areas for dates and paces
    private LinearLayout verticalLayoutDeadline;
    private LinearLayout verticalLayoutPace;

    // Set of buttons and views to pick and show deadline days values
    private EditText editTextDeadlineMinPace;
    private ImageButton buttonDeadlineMinPaceDatePicker;
    private EditText editTextDeadlineMaxPace;
    private ImageButton buttonDeadlineMaxPaceDatePicker;

    // Input fields to get dayly units in pace mode
    private EditText editTextUnitsPerDayMinPace;
    private EditText editTextUnitsPerDayMaxPace;

    // Button to create project
    private Button buttonCreateProject;

    /**
     * Keeps creation mode
     */
    private boolean isPaceMode = false;

    /**
     * Keeps command is it creation or editing mode
     */
    private String command;

    // Variables to store dates values
    private Date startDay;
    private Date deadlineMinPaceDate;
    private Date deadlineMaxPaceDate;

    /**
     * Link to the project being created or edited
     */
    private SmartProject formProject;

    /**
     * Date format to show dates on screen
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_form);

        initiateUI();

        // Receiving command to setup form mode
        command = getIntent().getStringExtra(MainActivity.COMMAND_TRANSMIT_NAME);

        // Setup foem mode
        if (command.equals(MainActivity.COMMAND_EDIT)) {
            // TODO populate fields
        } else {
            // do nothing
        }

    }


    /**
     * Method collects values from an inputs, creates a new project and opens project overview activity
     *
     * @param view
     */
    public void createNewProject(View view) {

        if (validateInput()) {

            if (command.equals(MainActivity.COMMAND_CREATE)) {

                String name = editTextProjectName.getText().toString().trim();
                String description = editTextProjectDescription.getText().toString().trim();
                int unitsTotal = Integer.parseInt(editTextUnitsTotalAmount.getText().toString());

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

    }


    /**
     * Validates user's input
     *
     * @return true - if all input fields are filled properly
     */
    private boolean validateInput() {

        int titleLimit = 70;
        int descriptionLimit = 280;

        // validate name
        if (editTextProjectName.getText().toString().trim().isEmpty()) {
            editTextProjectName.setError("Field can't be empty");
            return false;
        } else if (editTextProjectName.getText().toString().trim().length() > titleLimit) {
            editTextProjectName.setError("Title length should be less than or equals " + titleLimit + " characters");
            return false;
        } else {
            editTextProjectName.setError(null);
        }

        // validate total units amount
        if (editTextUnitsTotalAmount.getText().toString().trim().isEmpty()) {
            editTextUnitsTotalAmount.setError("Field can't be empty");
            return false;
        } else if (editTextUnitsTotalAmount.getText().toString().trim().length() > 10) {
            editTextUnitsTotalAmount.setError("String length should be no more than 10 characters");
            return false;
        } else if (Long.parseLong(editTextUnitsTotalAmount.getText().toString()) >= Integer.MAX_VALUE) {
            editTextUnitsTotalAmount.setError("Value should be less than 2147483647");
            return false;
        } else if (Long.parseLong(editTextUnitsTotalAmount.getText().toString()) <= 0) {
            editTextUnitsTotalAmount.setError("Value should be greater than zero");
            return false;
        } else {
            editTextUnitsTotalAmount.setError(null);
        }

        // validate description
        if (editTextProjectDescription.getText().toString().trim().isEmpty()) {
            editTextProjectDescription.setError("Field can't be empty");
            return false;
        } else if (editTextProjectDescription.getText().toString().trim().length() > descriptionLimit) {
            editTextProjectDescription.setError("Description length should be less than or equals " + descriptionLimit + " characters");
            return false;
        } else {
            editTextProjectDescription.setError(null);
        }

        // validate start day
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date tempToday = c.getTime();
        if (editTextStartDay.getText().toString().trim().isEmpty() || startDay == null) {
            editTextStartDay.setError("Field can't be empty");
            return false;
        } else if (startDay.before(tempToday)) {
            editTextStartDay.setError("Start day should be today or in the future");
            return false;
        } else {
            editTextStartDay.setError(null);
        }

        if (!isPaceMode) {
            // validate deadline days min pace
            if (editTextDeadlineMinPace.getText().toString().trim().isEmpty() || deadlineMinPaceDate == null) {
                editTextDeadlineMinPace.setError("Field can't be empty");
                return false;
            } else if (deadlineMinPaceDate.before(startDay) || deadlineMinPaceDate.equals(startDay)) {
                editTextDeadlineMinPace.setError("Deadline day with min pace should be AFTER Start day");
                return false;
            } else {
                editTextDeadlineMinPace.setError(null);
            }

            // validate deadline days max pace
            if (editTextDeadlineMaxPace.getText().toString().trim().isEmpty() || deadlineMinPaceDate == null) {
                editTextDeadlineMaxPace.setError("Field can't be empty");
                return false;
            } else if (!deadlineMaxPaceDate.after(startDay) || !deadlineMaxPaceDate.before(deadlineMinPaceDate)) {
                editTextDeadlineMaxPace.setError("Deadline day with max pace should be AFTER Start day AND BEFORE Deadline with min pace");
                return false;
            } else {
                editTextDeadlineMaxPace.setError(null);
            }

        } else {
            // check fields if empty
            if (editTextUnitsPerDayMinPace.getText().toString().trim().isEmpty()) {
                editTextUnitsPerDayMinPace.setError("Field can't be empty");
                return false;
            }
            if (editTextUnitsPerDayMaxPace.getText().toString().trim().isEmpty()) {
                editTextUnitsPerDayMaxPace.setError("Field can't be empty");
                return false;
            }
            // check fields if too long
            if (editTextUnitsPerDayMinPace.getText().toString().trim().length() > 10) {
                editTextUnitsPerDayMinPace.setError("String length should be no more than 10 characters");
                return false;
            }
            if (editTextUnitsPerDayMaxPace.getText().toString().trim().length() > 10) {
                editTextUnitsPerDayMaxPace.setError("String length should be no more than 10 characters");
                return false;
            }
            // check fields if contain zero value or greater than max integer
            if (Long.parseLong(editTextUnitsPerDayMinPace.getText().toString()) >= Integer.MAX_VALUE
                    || Long.parseLong(editTextUnitsPerDayMinPace.getText().toString()) <= 0) {
                editTextUnitsPerDayMinPace.setError("Value should be less than 2147483647\nand greater than 0");
                return false;
            }
            if (Long.parseLong(editTextUnitsPerDayMaxPace.getText().toString()) >= Integer.MAX_VALUE
                    || Long.parseLong(editTextUnitsPerDayMaxPace.getText().toString()) <= 0) {
                editTextUnitsPerDayMaxPace.setError("Value should be less than 2147483647\nand greater than 0");
                return false;
            }
            if (Integer.parseInt(editTextUnitsPerDayMaxPace.getText().toString())
                    <= Integer.parseInt(editTextUnitsPerDayMinPace.getText().toString())) {
                editTextUnitsPerDayMaxPace.setError("Should be greater than min pace per day value");
                return false;
            }

            editTextUnitsPerDayMinPace.setError(null);
            editTextUnitsPerDayMaxPace.setError(null);
        }

        return true;
    }


    /**
     * Picks a date from popup calendar view, setups date variables and display formatted dates on screen
     *
     * @param view
     */
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

                        switch (tag) {
                            // setup start date
                            case "date_picker_start":
                                startDay = calendar.getTime();
                                editTextStartDay.setText(dateFormat.format(startDay));
                                break;

                            // setup minimum pace deadline date
                            case "date_picker_min":
                                deadlineMinPaceDate = calendar.getTime();
                                editTextDeadlineMinPace.setText(dateFormat.format(deadlineMinPaceDate));
                                break;

                            // setup maximum pace deadline date
                            case "date_picker_max":
                                deadlineMaxPaceDate = calendar.getTime();
                                editTextDeadlineMaxPace.setText(dateFormat.format(deadlineMaxPaceDate));
                                break;
                        }


                    }
                }, cYear, cMonth, cDay);

        datePickerDialog.show();

    }


    /**
     * UI initialization
     */
    private void initiateUI() {

        // setup ActionBar title
        String actionTitle = "SMART form";
        if (getActionBar() != null) {
            getActionBar().setTitle(actionTitle);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionTitle);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextProjectName = findViewById(R.id.editTextProjectName);
        editTextUnitsTotalAmount = findViewById(R.id.editTextUnitsAmount);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);

        editTextStartDay = findViewById(R.id.editTextStartDay);
        editTextStartDay.setInputType(InputType.TYPE_NULL);
        editTextStartDay.setKeyListener(null);

        buttonStartDayPicker = findViewById(R.id.buttonStartDayPicker);
        buttonStartDayPicker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                startDay = SmartProject.getTodayOfMidnight();
                editTextStartDay.setText(dateFormat.format(startDay));

                return true;
            }
        });

        buttonStartDayPicker = findViewById(R.id.buttonStartDayPicker);

        radioGroupDeadlinePace = findViewById(R.id.radioGroupDeadlinePace);
        radioButtonDeadline = findViewById(R.id.radioButtonDeadline);
        radioButtonPace = findViewById(R.id.radioButtonPace);

        verticalLayoutDeadline = findViewById(R.id.verticalLayoutDeadline);
        verticalLayoutPace = findViewById(R.id.verticalLayoutPace);
        verticalLayoutPace.setVisibility(View.GONE);

        editTextDeadlineMinPace = findViewById(R.id.editTextDeadlineMinPace);
        editTextDeadlineMinPace.setInputType(InputType.TYPE_NULL);
        editTextDeadlineMinPace.setKeyListener(null);

        buttonDeadlineMinPaceDatePicker = findViewById(R.id.buttonDeadlineMinPaceDatePicker);

        editTextDeadlineMaxPace = findViewById(R.id.editTextDeadlineMaxPace);
        editTextDeadlineMaxPace.setInputType(InputType.TYPE_NULL);
        editTextDeadlineMaxPace.setKeyListener(null);

        buttonDeadlineMaxPaceDatePicker = findViewById(R.id.buttonDeadlineMaxPaceDatePicker);

        editTextUnitsPerDayMinPace = findViewById(R.id.editTextUnitsPerDayMinPace);
        editTextUnitsPerDayMaxPace = findViewById(R.id.editTextUnitsPerDayMaxPace);

        buttonCreateProject = findViewById(R.id.buttonCreateProject);

        radioGroupDeadlinePace.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RelativeLayout.LayoutParams params = new RelativeLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                if (radioButtonDeadline.isChecked()) {

                    isPaceMode = false;

                    verticalLayoutDeadline.setVisibility(View.VISIBLE);
                    verticalLayoutPace.setVisibility(View.GONE);

                    editTextUnitsPerDayMinPace.getText().clear();
                    editTextUnitsPerDayMaxPace.getText().clear();

                    params.addRule(RelativeLayout.BELOW, R.id.verticalLayoutDeadline);
                    buttonCreateProject.setLayoutParams(params);

                } else if (radioButtonPace.isChecked()) {

                    isPaceMode = true;

                    verticalLayoutDeadline.setVisibility(View.GONE);
                    verticalLayoutPace.setVisibility(View.VISIBLE);

                    editTextDeadlineMinPace.getText().clear();
                    editTextDeadlineMaxPace.getText().clear();

                    params.addRule(RelativeLayout.BELOW, R.id.verticalLayoutPace);
                    buttonCreateProject.setLayoutParams(params);

                } else {

                    String msg = "Something gone wrong with radio buttons";
                    Toast.makeText(ProjectFormActivity.this, msg, Toast.LENGTH_LONG).show();
                    Log.e("=== ERR >>>", msg);

                }

            }
        });

    }


}
