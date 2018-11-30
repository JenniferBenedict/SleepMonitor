package victorine.sleepmonitor;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Stats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_screen);
        ListView nightList = (ListView) findViewById(R.id.sleeplist);
        BarChart chart = (BarChart) findViewById(R.id.chart);
        TextView recommendation = (TextView) findViewById(R.id.recommendation);

        SleepRecord[] records = new SleepRecord[7];
        float totalSleepPast7Days = 0;

        //retrieve a list (sorted by timestamp) of all sleep records taken so far
        ArrayList<File> nightsList = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        Object[] nights = nightsList.toArray();
        Arrays.sort(nights);

        //handle, at most, only the last 7 sleep records
        for (int index = 0; index < nights.length && index < 7; index++){
            records[index] = new SleepRecord();

            File night = (File)nights[nights.length - index - 1];

            //retrieve the exact date of the sleep record
            String filename = night.getName();
            String timestamp = filename.substring(10,20);
            Date date = new java.util.Date(Long.valueOf(timestamp)*1000);
            records[index].setDay(date.getDay());
            records[index].setDate(new SimpleDateFormat("MM/dd").format(date));

            //retrieve the data from this sleep record
            String content = FileHandler.readFile(night);
            String[] parts = content.split(";");
            int num5SecondIntervals = parts.length - 1;

            //retrieve the total sleep (in hours) of this sleep record
            float totalSleep =  num5SecondIntervals * 5f / 3600f;
            totalSleepPast7Days += totalSleep;
            records[index].setTotalSleep(totalSleep);

            int movements = 0;
            int lightSleep = 0; // number of 25 minute intervals classified as light sleep
            int deepSleep = 0; // number of 25 minute intervals classified as deep sleep
            for (int i = 1; i < parts.length; i++) {
                String[] values = parts[i].split(" ");
                if (values[1].equals("2")) {
                    movements++;
                }

                if (i % 300 == 0 || i == parts.length - 1) {
                    //determine if this 25 minute interval was deep or light sleep
                    if (movements > 1) {
                        lightSleep++;
                    } else {
                        deepSleep++;
                    }
                    movements = 0;
                }
            }

            float percentLightSleep = lightSleep / (float) (lightSleep + deepSleep) * 100;
            float percentDeepSleep = deepSleep / (float) (lightSleep + deepSleep) * 100;
            records[index].setDeepSleep(percentDeepSleep);
            records[index].setLightSleep(percentLightSleep);
        }

        //compute average sleep for the past 7 records, and display appropriate recommendation
        float avgSleepHoursPast7Days = totalSleepPast7Days / Math.min(nights.length, 7);
        displayAverageSleepRecommendation(recommendation, avgSleepHoursPast7Days);

        //plot the overall sleep pattern and display individual sleep data
        plotSleepHours(chart, records);
        displaySleepDetails(nightList, records);
    }

    /**
     * Displays a recommendation to the user based on their avg sleep from the past 7 days.
     *
     * @param textView
     *      the TextView.
     * @param avgSleepPast7Days
     *      the average sleep from the past 7 days
     */
    private void displayAverageSleepRecommendation(TextView textView, float avgSleepPast7Days) {
        textView.setText("You've been getting an average of " + convertHours(avgSleepPast7Days) + " sleep over the last week. ");
        if (avgSleepPast7Days > 7){
            textView.append("Keep it up!");
            textView.setBackgroundColor(0xFF006600);
        } else if (avgSleepPast7Days > 5.5){
            textView.append("You may need a little more for optimal health.");
            textView.setBackgroundColor(0xFF0000ff);
        } else {
            textView.append("Try getting to bed earlier for a good night's rest.");
            textView.setBackgroundColor(0xFFd36b6b);
        }
    }

    /**
     * Plots the total number of hours slept during each of the past 7 records.
     *
     * @param chart
     *      The chart view.
     * @param data
     *      The last seven records of sleep.
     */
    private void plotSleepHours(BarChart chart, SleepRecord[] data){
        List<BarEntry> entries = new ArrayList<>();
        String[] xVals = new String[data.length];

        for (int i = 0; i < data.length; i++) {
            if (data[data.length - i - 1] != null){
                entries.add(new BarEntry(i, data[data.length - i - 1].getTotalSleep()));
                xVals[i] = getCorrespondingDay(data[data.length - i - 1].getDay());
            }
            else {
                entries.add(new BarEntry(i, null));
                xVals[i] = "";
            }
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Total Sleep (in hours)");
        barDataSet.setColor(Color.parseColor("#37ac46"));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setValueTextSize(14f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false); // no axis labels
        yAxis.setDrawAxisLine(false); // no axis line
        yAxis.setDrawGridLines(false); // no grid lines
        yAxis.setDrawZeroLine(true); // draw a zero line
        yAxis.setAxisMaximum(barDataSet.getYMax() + 0.5f);
        yAxis.setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false); // no right axis

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false); // no grid lines
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xVals));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setHardwareAccelerationEnabled(true);
        chart.animateY(1000);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);

        chart.setData(barData);

        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);

        chart.invalidate(); // refresh
    }

    /**
     * Displays the details for each of the sleep records from the past week.
     *
     * @param list
     *      The list view
     * @param data
     *      The last seven records of sleep.
     */
    private void displaySleepDetails(ListView list, SleepRecord[] data){
        ArrayList<String> listItems = new ArrayList<>();
        for (int i = 0; i < data.length; i++){
            if (data[i] != null){
                String label = getCorrespondingDay(data[i].getDay())
                        + ", " + data[i].getDate()
                        + "\n   Total Sleep: " + convertHours(data[i].getTotalSleep())
                        + "\n   Deep Sleep: " + String.format("%.0f", data[i].getDeepSleep()) + "%"
                        + "\n   Light Sleep: " + String.format("%.0f", data[i].getLightSleep()) + "%";
                listItems.add(label);
            }
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.row_view,listItems);
        list.setAdapter(listAdapter);
    }

    /**
     * Convert a decimal number of hours to the number of hours and minutes it represents
     *
     * @param hours
     *      a decimal number of hours.
     * @return
     *      a string representation in hours and minutes.
     */
    private String convertHours(float hours){
        int numHours = (int) hours;
        int numMinutes = (int)((hours - numHours) * 60);
        return numHours + "h " + numMinutes + "m";
    }

    /**
     * Maps an integer from 0-6 to the corresponding day of the week
     *
     * @param day
     *      An integer from 0-6.
     * @return
     *      A string representing the appropriate day of the week (0 --> Sun, 1 --> Mon, etc.)
     */
    private String getCorrespondingDay(int day){
        switch (day){
            case 1:
                return "Mon";
            case 2:
                return "Tues";
            case 3:
                return "Wed";
            case 4:
                return "Thurs";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            case 0:
                return "Sun";
            default:
                return null;
        }
    }

    /**
     * Maps integer xAxis labels to appropriate string labels
     */
    private class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }
    }
}
