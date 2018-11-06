package victorine.sleepmonitor;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_screen);
        ArrayAdapter<Float> listAdapter;
        ListView nightList = (ListView) findViewById(R.id.sleeplist);
        LineChart chart = (LineChart) findViewById(R.id.chart);

        //TODO: change this data to the user's actual data
        Float[] hoursSlept = new Float[]{6.5f, 7.0f, 8.0f, 7.5f, 8.0f, 6.0f, 6.5f};
        plotSleepHours(chart, hoursSlept);

        /** Converts list of floats of hours slept into arraylist
         * to be formatted in a list under the chart
         */
        ArrayList<Float> hoursSleptList = new ArrayList<Float>();
        hoursSleptList.addAll(Arrays.asList(hoursSlept));
        listAdapter = new ArrayAdapter<Float>(this, R.layout.row_view,hoursSleptList);
        nightList.setAdapter(listAdapter);
    }


    /**
     * Plots the number of hours slept during each day of the previous week.
     *
     * @param chart
     *      The chart view.
     * @param data
     *      The number of hours slept for the last 7 days.
     */
    private void plotSleepHours(LineChart chart, Float[] data){
        List<Entry> entries = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            entries.add(new Entry((float)day + 1, data[day]));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Sleep");
        LineData lineData = new LineData(dataSet);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(7f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
