package victorine.sleepmonitor;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

        SleepRecord[] records = new SleepRecord[7];

        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        for (int index = 0; index < nights.size() && index < 7; index++){
            records[index] = new SleepRecord();

            File night = nights.get(nights.size() - index - 1);
            String filename = night.getName();
            String timestamp = filename.substring(10,20);
            long dv = Long.valueOf(timestamp)*1000;
            Date date = new java.util.Date(dv);

            String content = FileHandler.readFile(night);

            String[] parts = content.split(";");
            int num5SecondIntervals = parts.length - 1;

            records[index].setDay(date.getDay());
            records[index].setDate(new SimpleDateFormat("MM/dd").format(date));

            // 25 minute intervals
            int[] intervals = new int[(int)Math.ceil(parts.length/300f)];

            int movements = 0;

            int awake = 0;
            int sleep = 0;

            for(int i = 1; i<parts.length; i++) {
                String[] values = parts[i].split(" ");
                if(values[1].equals("2")) {
                    movements++;
                }

                if(i % 300 == 0 || i == parts.length - 1) {
                    // Add the movement interval
                    if (movements > 1) {
                        intervals[(int) (i / 300f)] = movements;
                        awake++;
                    } else {
                        sleep++;
                    }
                    movements = 0;
                }
            }

            int phases = 0;
            boolean isSleeping = false;

            for(int i = 0;i<intervals.length;i++) {
                int movementAmount = 0;
                if(intervals[i] > 2) {
                    movementAmount = intervals[i];
                }

                if(movementAmount > 2) {
                    if(isSleeping) {
                        phases++;
                        isSleeping = false;
                    }
                } else {
                    if(!isSleeping) {
                        isSleeping = true;
                    }
                }
            }

            int qualityPhases = 1;
            // Too much phases are no good sign
            if(phases > 10 || phases < 4) {
                qualityPhases = 0;
            }

            int qualitySleep = -1;
            if(parts.length >= 0.2 * 60*60*7) {
                // At least 7 hours of sleep
                qualitySleep = 1;
            } else if(parts.length >= 0.2 * 60*60*5.5) {
                // At least 5.5 hours of sleep
                qualitySleep = 0;
            }

            float totalHoursSlept = num5SecondIntervals * 5f / 3600f;
            float percentLightSleep = awake / (float)(awake + sleep);
            float percentDeepSleep = sleep / (float)(awake + sleep);

            records[index].setTotalSleep(totalHoursSlept);
            records[index].setDeepSleep(percentDeepSleep);
            records[index].setLightSleep(percentLightSleep);
        }

        plotSleepHours(chart, records);
        displaySleepDetails(nightList, records);
    }


    /**
     * Plots the number of hours slept during each day of the previous week.
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
            if (data[i] != null){
                entries.add(new BarEntry(i, data[data.length - i - 1].getTotalSleep()));
                xVals[i] = data[data.length - i - 1].getDate();
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
        yAxis.setAxisMaximum(barDataSet.getYMax());
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
        ArrayList<String> listItems = new ArrayList<String>();
        for (int i = 0; i < data.length; i++){
            if (data[i] != null){
                String label = getCorrespondingDay(data[i].getDay())
                        + ", " + data[i].getDate()
                        + "\n   Total hours slept: " + String.format("%.3f", data[i].getTotalSleep())
                        + "\n   Deep Sleep %: " + String.format("%.3f", data[i].getDeepSleep())
                        + "\n   Light Sleep %: " + String.format("%.3f", data[i].getLightSleep());
                listItems.add(label);
            }
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.row_view,listItems);
        list.setAdapter(listAdapter);
    }

    /**
     * Maps an integer from 0-6 to the corresponding day of the week
     *
     * @param day
     *      An integer from 0-6.
     * @return
     *      A string representing the appropriate day of the week (0 --> Sunday, 1 --> Monday, etc.)
     */
    private String getCorrespondingDay(int day){
        switch (day){
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 0:
                return "Sunday";
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
