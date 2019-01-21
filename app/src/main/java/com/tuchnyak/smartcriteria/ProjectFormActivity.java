package com.tuchnyak.smartcriteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ProjectFormActivity extends AppCompatActivity {

    private EditText editTextProjectName;
    private EditText editTextUnitsAmount;
    private EditText editTextProjectDescription;

    private EditText editTextStartDay;
    private ImageButton buttonStartDayPicker;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_form);

        initiateUI();

        command = getIntent().getStringExtra(MainActivity.COMMAND_TRANSMIT_NAME);

    }


    private void initiateUI() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextProjectName = findViewById(R.id.editTextProjectName);
        editTextUnitsAmount = findViewById(R.id.editTextUnitsAmount);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);

        editTextStartDay = findViewById(R.id.editTextStartDay);
        buttonStartDayPicker = findViewById(R.id.buttonStartDayPicker);

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

        RelativeLayout.LayoutParams params= new RelativeLayout
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
