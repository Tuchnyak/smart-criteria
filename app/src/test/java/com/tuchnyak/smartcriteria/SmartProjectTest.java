package com.tuchnyak.smartcriteria;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuchnyak.smartcriteria.entity.SmartProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

public class SmartProjectTest {

    private SmartProject smp;
    private String marshaledProjectString;

    @Before
    public void setup() {

        initiateProject();

    }

    @After
    public void tearDown() {

    }


    /**
     * Сhecking project structure
     */
    @Test
    public void projectStructureTest() {

        // check dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date startDay = null;
        Date deadlineMinPace = null;
        Date deadlineMaxPace = null;
        Date someDay = null;

        try {
            startDay = dateFormat.parse("21-08-2018");
            deadlineMinPace = dateFormat.parse("27-08-2018");
            deadlineMaxPace = dateFormat.parse("25-08-2018");
            someDay = dateFormat.parse("24-08-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals(startDay, smp.getStartDay());
        assertEquals(deadlineMinPace, smp.getDeadlineDayMinPace());
        assertEquals(deadlineMaxPace, smp.getDeadlineDayMaxPace());

        if (startDay != null) assertEquals(startDay.getTime(), smp.getStartDay().getTime());

        assertEquals(someDay, smp.getEntriesMinPace().keySet().toArray()[3]);
        System.out.println("******************* SD " + someDay);
        System.out.println("******************* DP " + smp.getEntriesMinPace().keySet().toArray()[3]);

        assertEquals(5, smp.getAmountOfDaysMaxPace());


    }


    /**
     * Сhecking SmartProject marshaling and unmarshaling with Jackson
     */
    @Test
    public void marshalProjectTest() {

        ObjectMapper om = new ObjectMapper();

        //********* checking marshaling
        try {
            marshaledProjectString = om.writeValueAsString(smp);

            // print pretty json
            System.out.println("===>>> JSON:");
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(smp));

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        assertNotNull(marshaledProjectString);
        assertNotEquals(marshaledProjectString, 0, marshaledProjectString.length());
        assertTrue(marshaledProjectString.contains("name"));


        //********* checking unmarshalling
        SmartProject restoredProject = null;

        try {
            restoredProject = om.readValue(marshaledProjectString, SmartProject.class);

            System.out.println("\n===>>> Restored:" + restoredProject);

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(restoredProject);
        assertEquals(smp.getStartDay(), restoredProject.getStartDay());
        assertEquals(smp.getDaysAsStringArray().length, restoredProject.getDaysAsStringArray().length);
        assertArrayEquals(smp.getDaysAsStringArray(), restoredProject.getDaysAsStringArray());
        assertEquals(smp.getAmountOfDaysMaxPace(), restoredProject.getAmountOfDaysMaxPace());
        if (smp.getEntriesMinPace().size() > 3) {
            assertEquals(smp.getEntriesMinPace().keySet().toArray()[3],
                    restoredProject.getEntriesMinPace().keySet().toArray()[3]);
        }
        assertNotEquals(smp, restoredProject);

    }


    /**
     * Initiate and print to a console project instance for testing
     */
    private void initiateProject() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date startDay = null;
        Date deadlineMinPace = null;
        Date deadlineMaxPace = null;

        try {
            startDay = dateFormat.parse("21-08-2018");
            deadlineMinPace = dateFormat.parse("27-08-2018");
            deadlineMaxPace = dateFormat.parse("25-08-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDay != null && deadlineMinPace != null && deadlineMaxPace != null) {

            smp = new SmartProject("Test project", "Test project description",
                    35, startDay, deadlineMinPace, deadlineMaxPace);

            System.out.println("===>>>" + smp.toString());

        } else {
            System.out.println("===>>> Some date are null!");
        }

        /*
        // *** print block ***
        smp.printEntries();

        for (float f : smp.getYAxisChartValuesMinPace()) {
            System.out.println(f);
        }

        for (float f : smp.getYAxisChartValuesMaxPace()) {
            System.out.println(f);
        }

        for (float f : smp.getFloatNumbersForXAxis()) {
            System.out.println(f);
        }
        */

    }

}
