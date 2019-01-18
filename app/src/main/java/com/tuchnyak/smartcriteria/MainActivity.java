package com.tuchnyak.smartcriteria;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.tuchnyak.smartcriteria.entity.SmartProject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    /**
     * List to keep all projects
     */
    public static List<SmartProject> smartProjectList;
    public static List<String> smartProjectNameList;

    public static ArrayAdapter<String> adapter;

    private ListView listProjectsView;
    private SharedPreferences sharedPreferences;

    private static final String STRING_TO_SAVE_PROJECT_LIST = "projectsList";
    public static final String PROJECT_ID_STRING_NAME = "projectId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = this
                .getSharedPreferences("com.tuchnyak.smartcriteria", Context.MODE_PRIVATE);

        listProjectsView = findViewById(R.id.listProjects);

        // restore projects from shared preferences
        smartProjectList = restoreSharedProjects();

        smartProjectNameList = new ArrayList<>();

        if (smartProjectList == null || smartProjectList.isEmpty()) {

            smartProjectList = new ArrayList<>();

            // TODO: delete after implementation of a project creation process
            SmartProject tempProject = initiateProject();
            smartProjectList.add(tempProject);
            smartProjectNameList.add(tempProject.getName());

            Toast.makeText(this, "There is a lot of space for new projects!\nBe SMART! Goodluck!", Toast.LENGTH_LONG).show();

        } else {

            // populate smartProjectNameList
            for (SmartProject project : smartProjectList) {
                smartProjectNameList.add(project.getName());
            }

        }

        // setup array adapter and set it to a ListView
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, smartProjectNameList
        );

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {

                // save to shared preferences
                saveSharedProjects();

                super.onChanged();
            }
        });

        listProjectsView.setAdapter(adapter);

        // setting up a list items behaviour on click and long press
        listProjectsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, "Open project...", Toast.LENGTH_SHORT).show();

                // sending list item position to a new activity
                openProjectOverviewActivity(position);

            }
        });

        listProjectsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // call alert and delete project if user agree
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete project!")
                        .setMessage("Are you really want to delete this project?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton(
                                "Delete",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        smartProjectList.remove(position);
                                        smartProjectNameList.remove(position);
                                        adapter.notifyDataSetChanged();

                                    }
                                })
                        .show();

                return false;
            }
        });


    }


    /**
     * Open project from projects list in ProjectOverviewActivity
     *
     * @param position project's position in projects list
     */
    private void openProjectOverviewActivity(int position) {

        Intent intent = new Intent(getApplicationContext(), ProjectOverviewActivity.class);
        intent.putExtra(PROJECT_ID_STRING_NAME, position);
        startActivity(intent);

    }

    /**
     * Save list of project to shared preferences
     */
    private void saveSharedProjects() {

        ObjectMapper om = new ObjectMapper();

        try {

            sharedPreferences.edit()
                    .putString(STRING_TO_SAVE_PROJECT_LIST,
                            om.writeValueAsString(smartProjectList)).apply();

        } catch (JsonProcessingException e) {

            e.printStackTrace();
            Log.e("=== ERR >>>", "PROBLEM DURING SAVING SHARED PREFERENCES!");

        }

    }


    /**
     * Restoring saved list of projects from shared preferences
     *
     * @return ArrayList of SmartProject
     */
    private ArrayList<SmartProject> restoreSharedProjects() {

        String stringProjects = sharedPreferences.getString(STRING_TO_SAVE_PROJECT_LIST, "");

        ArrayList<SmartProject> projects = null;

        try {

            ObjectMapper om = new ObjectMapper();

            CollectionType collectionType = om.getTypeFactory()
                    .constructCollectionType(ArrayList.class, SmartProject.class);

            projects = om.readValue(stringProjects, collectionType);

        } catch (IOException e) {

            e.printStackTrace();
            Log.e("=== ERR >>>", "PROBLEM DURING RESTORING PROJECTS FROM SHARED PREFERENCES!");

        }

        return projects;
    }


    // TODO: delete after implementation of a project creation process
    @Deprecated
    private SmartProject initiateProject() {

        SmartProject smartProject = null;

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

        return smartProject;
    }


}
