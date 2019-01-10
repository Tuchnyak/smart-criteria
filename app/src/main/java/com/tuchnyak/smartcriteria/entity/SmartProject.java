package com.tuchnyak.smartcriteria.entity;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Class for project describing.<br>
 * Lists hold units in decreasing amounts for burnout charts.<br>
 * <i><b>Attention!</b> An object of this class stores raw double and float values!
 * Handle it outside for beautiful and human-friendly charts.<br>
 * For example:<br>
 * unitsPerDayMinPace = 3.451<br>
 * entry for may 24th could stores: 18.42</i>
 */
public class SmartProject {

    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int ONE_DAY_IN_MILLIS = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND;

    /**
     * Project name and description.
     * Name's chars limit - about 100 symbols.
     * Description should be limited - 300 chars for instance.
     */
    private String name;
    private String description;

    /**
     * Total amount of units that should be done. Lectures, pages, videos, pieces...
     */
    private int unitsTotal;

    /**
     * Day describing first day of a project.
     */
    private Date startDay;

    /**
     * Variable to describe project's deadline made by slow and high pace.
     */
    private Date deadlineDayMinPace;
    private Date deadlineDayMaxPace;

    /**
     * Amount of days for minimum and maximum pace - maximum and minimum project length.
     */
    private long amountOfDaysMinPace;
    private long amountOfDaysMaxPace;

    /**
     * Amount of project's units that should be done in one day during min and max pace.
     */
    private double unitsPerDayMinPace;
    private double unitsPerDayMaxPace;

    /**
     * Collections of units per day for a minimum pace, maximum pace and current project charts.
     */
    private Map<Date, Float> entriesMinPace;
    private Map<Date, Float> entriesMaxPace;
    private Map<Date, Float> entriesCurrentPace;

    /**
     * Variable to store current progress of a project
     */
    private long currentProgress = 0;

    /**
     * Variable to store project state - finished or not
     */
    private boolean isFinished = false;


    public SmartProject() {
    }

    /**
     * Constructor to create an instance of a Smart project when there is a necessity
     * to calculate a number of units that should be done in one day and amount of days
     * for a project in both paces from two known deadline days.
     *
     * @param name               title of the project
     * @param description        project's description
     * @param unitsTotal         total units amount to be solved in the project
     * @param startDay           first day of the project
     * @param deadlineDayMinPace the day when all units should be done in min pace
     * @param deadlineDayMaxPace the day when all units should be done in max pace
     */
    public SmartProject(String name, String description, int unitsTotal,
                        Date startDay, Date deadlineDayMinPace, Date deadlineDayMaxPace) {

        this.name = name;
        this.description = description;
        this.unitsTotal = unitsTotal;
        this.startDay = startDay;
        this.deadlineDayMinPace = deadlineDayMinPace;
        this.deadlineDayMaxPace = deadlineDayMaxPace;

        // calculating the number of days required to complete a project at both paces
        amountOfDaysMinPace = 1 + getGapDays(startDay, deadlineDayMinPace);
        amountOfDaysMaxPace = 1 + getGapDays(startDay, deadlineDayMaxPace);

        // calculating units per day for both paces
        unitsPerDayMinPace = (double) unitsTotal / amountOfDaysMinPace;
        unitsPerDayMaxPace = (double) unitsTotal / amountOfDaysMaxPace;

        // calculating and populating lists for charts
        populateListsOfEntries(unitsTotal, startDay, unitsPerDayMinPace, unitsPerDayMaxPace);


    }

    /**
     * Constructor to create an instance of a Smart project when there is a necessity
     * to calculate deadline days and amount of days for a project in both paces
     * from a known number of units that should be done in one day.
     *
     * @param name               title of the project
     * @param description        project's description
     * @param unitsTotal         total units amount to be solved in the project
     * @param startDay           first day of the project
     * @param unitsPerDayMinPace amount of units which should be made in one day in minimum pace
     * @param unitsPerDayMaxPace amount of units which should be made in one day in maximum pace
     */
    public SmartProject(String name, String description, int unitsTotal,
                        Date startDay, double unitsPerDayMinPace, double unitsPerDayMaxPace) {

        this.name = name;
        this.description = description;
        this.unitsTotal = unitsTotal;
        this.startDay = startDay;
        this.unitsPerDayMinPace = unitsPerDayMinPace;
        this.unitsPerDayMaxPace = unitsPerDayMaxPace;

        // calculating the number of days required to complete a project at both paces
        amountOfDaysMinPace = (long) Math.ceil(unitsTotal / unitsPerDayMinPace);
        amountOfDaysMaxPace = (long) Math.ceil(unitsTotal / unitsPerDayMaxPace);

        // calculating min and max pace deadline dates
        deadlineDayMinPace = new Date(startDay.getTime()
                + amountOfDaysMinPace * ONE_DAY_IN_MILLIS - ONE_DAY_IN_MILLIS);
        deadlineDayMaxPace = new Date(startDay.getTime()
                + amountOfDaysMaxPace * ONE_DAY_IN_MILLIS - ONE_DAY_IN_MILLIS);

        // calculating and populating lists for charts
        populateListsOfEntries(unitsTotal, startDay, unitsPerDayMinPace, unitsPerDayMaxPace);

    }


    /**
     * Initializing and populating lists of entries.
     *
     * @param unitsTotal         total units amount to be solved in the project
     * @param startDay           first day of the project
     * @param unitsPerDayMinPace amount of units which should be made in one day in minimum pace
     * @param unitsPerDayMaxPace amount of units which should be made in one day in maximum pace
     */
    private void populateListsOfEntries(float unitsTotal, Date startDay,
                                        double unitsPerDayMinPace, double unitsPerDayMaxPace) {

        entriesMinPace = new TreeMap<>();
        entriesMaxPace = new TreeMap<>();
        entriesCurrentPace = new TreeMap<>();

        float tempUnitsTotalMin = unitsTotal;
        float tempUnitsTotalMax = unitsTotal;
        Date tempDate = new Date(startDay.getTime());

        for (int i = 0; i < amountOfDaysMinPace; i++) {

            tempUnitsTotalMin = (tempUnitsTotalMin - unitsPerDayMinPace) < 1
                    ? 0 : (float) (tempUnitsTotalMin - unitsPerDayMinPace);
            entriesMinPace.put(new Date(tempDate.getTime()), tempUnitsTotalMin);

            if (i < amountOfDaysMaxPace) {
                tempUnitsTotalMax = (tempUnitsTotalMax - unitsPerDayMaxPace) < 1
                        ? 0 : (float) (tempUnitsTotalMax - unitsPerDayMaxPace);
                entriesMaxPace.put(new Date(tempDate.getTime()), tempUnitsTotalMax);
            }

            tempDate.setTime(tempDate.getTime() + ONE_DAY_IN_MILLIS);

        }

    }


    /**
     * Checking existence of gap days and fill them accordingly to current progress.
     */
    public void checkAndFillGaps() {

        if (entriesCurrentPace.size() == 0) {

            fillGaps(startDay);

        } else {

            TreeMap<Date, Float> tempEntriesCurrentPace = (TreeMap<Date, Float>) entriesCurrentPace;
            Date lastDay = new Date(tempEntriesCurrentPace.lastKey().getTime());

            fillGaps(lastDay);

        }


    }


    /**
     * Fill gaps between today and last mapped day
     *
     * @param lastDay the day from which it is needed to fill gap days
     */
    private void fillGaps(Date lastDay) {

        if (getTodayOfMidnight().equals(lastDay)) {

            entriesCurrentPace.put(getTodayOfMidnight(), (float) unitsTotal - currentProgress);

        } else {

            long gapDays = getGapDays(lastDay, getTodayOfMidnight());
            float gapRemainder = (float) unitsTotal - currentProgress;

            Date tempDate = new Date(lastDay.getTime());

            for (int i = 0; i <= gapDays; i++) {
                entriesCurrentPace.put(new Date(tempDate.getTime()), gapRemainder);
                tempDate.setTime(tempDate.getTime() + ONE_DAY_IN_MILLIS);

            }

        }

    }


    /**
     * Calculating of the amount of date between two dates.
     *
     * @param earlier earlier date, for example September 1th
     * @param later   later date, for example September 2th
     * @return later minus earlier equals 1 gap day
     */
    private long getGapDays(Date earlier, Date later) {

        return TimeUnit.DAYS
                .convert(later.getTime() - earlier.getTime(),
                        TimeUnit.MILLISECONDS);
    }


    public void increaseCurrentProgress() {

        if (!isFinished) {

            currentProgress++;

            if (entriesCurrentPace.isEmpty()) {
                entriesCurrentPace = new TreeMap<>();

            }

            entriesCurrentPace.put(getTodayOfMidnight(), (float) unitsTotal - currentProgress);

            if (currentProgress == unitsTotal) isFinished = true;

        }

    }

    /**
     * @return today Date() with time: 0 hours, 0 minutes, 0 seconds and 0 milliseconds.
     */
    private Date getTodayOfMidnight() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


    /**
     * @return String[] days formatted by pattern: "dd MMM"
     */
    public String[] getDaysAsStringArray() {

        String[] days;
        Map<Date, Float> tempMap;

        // compare lengths of min and current map paces
        if (entriesMinPace.size() >= entriesCurrentPace.size()) {
            days = new String[entriesMinPace.size()];
            tempMap = entriesMinPace;

        } else {
            days = new String[entriesCurrentPace.size()];
            tempMap = entriesCurrentPace;

        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        int i = 0;
        for (Map.Entry<Date, Float> entry : tempMap.entrySet()) {
            days[i] = dateFormat.format(entry.getKey());
            i++;
        }

        return days;
    }


    /**
     * @return float[] for X axis numeration
     */
    public float[] getFloatNumbersForXAxis() {

        int count;

        // compare lengths of min and current map paces
        if (entriesMinPace.size() >= entriesCurrentPace.size()) {
            count = entriesMinPace.size();

        } else {
            count = entriesCurrentPace.size();

        }

        float[] numbers = new float[count];

        for (int i = 0; i < count; i++) {
            numbers[i] = (float) i;
        }

        return numbers;
    }


    /**
     * @return float[] values for min pace charts
     */
    public float[] getYAxisChartValuesMinPace() {

        return getFloatsForAxisFromEntries(entriesMinPace);
    }


    /**
     * @return float[] values for max pace charts
     */
    public float[] getYAxisChartValuesMaxPace() {

        return getFloatsForAxisFromEntries(entriesMaxPace);
    }


    /**
     * @return float[] values for current pace charts
     */
    public float[] getYAxisChartValuesCurrentPace() {

        return getFloatsForAxisFromEntries(entriesCurrentPace);
    }


    /**
     * Method to extract values for charts from storing maps.
     *
     * @param entriesMap - map with data
     * @return float[] - extracted float values for Y Axis
     */
    private float[] getFloatsForAxisFromEntries(Map<Date, Float> entriesMap) {

        Float[] objArr = entriesMap.values().toArray(new Float[0]);

        float[] floats = new float[objArr.length];

        for (int i = 0; i < floats.length; i++) {
            floats[i] = objArr[i];
        }

        return floats;
    }


    //TODO: Move test to unit tests!!!!
    @Deprecated
    public void printEntries() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());

        StringBuilder sb = new StringBuilder();

        sb.append("\nGenerated dates:\n").append("Minimum pace chart values:\n");

        for (Map.Entry<Date, Float> entry : entriesMinPace.entrySet()) {
            sb.append(dateFormat.format(entry.getKey())).append(" : ").append(entry.getValue()).append("\n");
        }

        sb.append("\nMaximum pace chart values:\n");
        for (Map.Entry<Date, Float> entry : entriesMaxPace.entrySet()) {
            sb.append(dateFormat.format(entry.getKey())).append(" : ").append(entry.getValue()).append("\n");
        }

        Log.i("***===>>> TEST values", sb.toString());

        sb = new StringBuilder();
        sb.append("\nDays in string array:\n");
        for (String day : getDaysAsStringArray()) {
            sb.append(day).append("\n");
        }

        Log.i("***===>>> TEST values", sb.toString());

    }


    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnitsTotal() {
        return unitsTotal;
    }

    public void setUnitsTotal(int unitsTotal) {
        this.unitsTotal = unitsTotal;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getDeadlineDayMinPace() {
        return deadlineDayMinPace;
    }

    public void setDeadlineDayMinPace(Date deadlineDayMinPace) {
        this.deadlineDayMinPace = deadlineDayMinPace;
    }

    public Date getDeadlineDayMaxPace() {
        return deadlineDayMaxPace;
    }

    public void setDeadlineDayMaxPace(Date deadlineDayMaxPace) {
        this.deadlineDayMaxPace = deadlineDayMaxPace;
    }

    public long getAmountOfDaysMinPace() {
        return amountOfDaysMinPace;
    }

    public void setAmountOfDaysMinPace(long amountOfDaysMinPace) {
        this.amountOfDaysMinPace = amountOfDaysMinPace;
    }

    public long getAmountOfDaysMaxPace() {
        return amountOfDaysMaxPace;
    }

    public void setAmountOfDaysMaxPace(long amountOfDaysMaxPace) {
        this.amountOfDaysMaxPace = amountOfDaysMaxPace;
    }

    public double getUnitsPerDayMinPace() {
        return unitsPerDayMinPace;
    }

    public void setUnitsPerDayMinPace(double unitsPerDayMinPace) {
        this.unitsPerDayMinPace = unitsPerDayMinPace;
    }

    public double getUnitsPerDayMaxPace() {
        return unitsPerDayMaxPace;
    }

    public void setUnitsPerDayMaxPace(double unitsPerDayMaxPace) {
        this.unitsPerDayMaxPace = unitsPerDayMaxPace;
    }

    public Map<Date, Float> getEntriesMinPace() {
        return entriesMinPace;
    }

    public void setEntriesMinPace(Map<Date, Float> entriesMinPace) {
        this.entriesMinPace = entriesMinPace;
    }

    public Map<Date, Float> getEntriesMaxPace() {
        return entriesMaxPace;
    }

    public void setEntriesMaxPace(Map<Date, Float> entriesMaxPace) {
        this.entriesMaxPace = entriesMaxPace;
    }

    public Map<Date, Float> getEntriesCurrentPace() {
        return entriesCurrentPace;
    }

    public void setEntriesCurrentPace(Map<Date, Float> entriesCurrentPace) {
        this.entriesCurrentPace = entriesCurrentPace;
    }

    public long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = currentProgress;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public String toString() {
        return "\nSmartProject{" +
                "\nname='" + name + '\'' +
                ", \ndescription='" + description + '\'' +
                ", \nunitsTotal=" + unitsTotal +
                ", \nstartDay=" + startDay +
                ", \ndeadlineDayMinPace=" + deadlineDayMinPace +
                ", \ndeadlineDayMaxPace=" + deadlineDayMaxPace +
                ", \namountOfDaysMinPace=" + amountOfDaysMinPace +
                ", \namountOfDaysMaxPace=" + amountOfDaysMaxPace +
                ", \nunitsPerDayMinPace=" + unitsPerDayMinPace +
                ", \nunitsPerDayMaxPace=" + unitsPerDayMaxPace +
                ", \ncurrentProgress=" + currentProgress +
                "\n}";
    }


}
