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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    /**
     * List to keep and show all projects names
     */
    public static List<String> smartProjectNameList;

    /**
     * Adapter for list of projects names
     */
    public static ArrayAdapter<String> adapter;

    /**
     * ListView to show all project names and manage them
     */
    private ListView listProjectsView;

    /**
     * Shared preferences to store projects permanently
     */
    private SharedPreferences sharedPreferences;

    /**
     * String constant to put and get a list of projects with shared preferences
     */
    private static final String STRING_TO_SAVE_PROJECT_LIST = "projectsList";

    /**
     * String constants to transmit certain project by id to another activity
     */
    public static final String PROJECT_ID_TRANSMIT_NAME = "projectId";
    public static final String COMMAND_TRANSMIT_NAME = "command";

    // Command constants for project form activity
    public static final String COMMAND_CREATE = "create";
    public static final String COMMAND_EDIT = "edit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get shared preferences reference to restore projects
        sharedPreferences = this
                .getSharedPreferences("com.tuchnyak.smartcriteria", Context.MODE_PRIVATE);

        listProjectsView = findViewById(R.id.listProjects);

        // restore projects from shared preferences
        smartProjectList = restoreSharedProjects();

        // list to show project names on a screen
        smartProjectNameList = new ArrayList<>();

        // check the presence of a stored projects
        if (smartProjectList == null || smartProjectList.isEmpty()) {

            smartProjectList = new ArrayList<>();

            Toast.makeText(this, "There is a lot of space for new projects!\nBe SMART! Goodluck!", Toast.LENGTH_LONG).show();

        } else {

            // populate smartProjectNameList
            int i = 0;
            for (SmartProject project : smartProjectList) {

                StringBuilder sb = new StringBuilder();

                sb.append(project.getName()).append(": ");

                SmartProject tempProject = smartProjectList.get(i);
                int percentageOfDone = (int) tempProject.getCurrentProgress() * 100 / tempProject.getUnitsTotal();

                sb.append(percentageOfDone).append("%");

                smartProjectNameList.add(sb.toString());

                i++;
            }

        }

        // setup array adapter and set it to a ListView
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, smartProjectNameList
        );

        // save projects in case of changes
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
                        .setTitle("Delete project?")
                        .setMessage("Are you really want to delete this project?")
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
                        .setNegativeButton("Cancel", null)
                        .show();

                return true;
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
        intent.putExtra(PROJECT_ID_TRANSMIT_NAME, position);
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


    /**
     * Save project on pause
     */
    @Override
    protected void onPause() {

        saveSharedProjects();

        super.onPause();
    }


    /**
     * Save project on back pressed
     */
    @Override
    public void onBackPressed() {

        saveSharedProjects();

        super.onBackPressed();
    }


    /**
     * Create menu
     *
     * @param menu dot menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Managing actions by clicked menu
     * @param item clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.createNewProjectMenuItem:
                openFormProject(COMMAND_CREATE);
                break;
            case R.id.wikiMenuItem:
                openWebView();
                break;
        }

        return true;
    }


    /**
     * Action by floating button
     * @param view clicked screen item
     */
    public void createNewProject(View view) {

        openFormProject(COMMAND_CREATE);

    }


    /**
     * Open project form activity to create new project
     *
     * @param command transmitted command for project form activity
     */
    private void openFormProject(String command) {

        Intent intent = new Intent(getApplicationContext(), ProjectFormActivity.class);
        intent.putExtra(COMMAND_TRANSMIT_NAME, command);
        startActivity(intent);

    }


    /**
     * Openweb view to download Wiki-article
     */
    private void openWebView() {
        //TODO
    }


}
