package victorine.sleepmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import de.sopamo.uni.sleepminder.lib.Recorder;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button stats;
    ImageView recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordButton = (ImageView ) findViewById(R.id.toggleRecording);
        stats = (Button) findViewById(R.id.statsbutton);

        if(!isExternalStorageWritable()) {
            new AlertDialog.Builder(this)
                    .setTitle("Caution")
                    .setMessage("The storage is not accessable. Please make sure to insert your sd-card and restart the app.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        //Initialize the record button to start or stop
        setRecordButton(SleepMonitor.recorder.isRunning());

        recordButton.setOnClickListener(this);
        stats.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.statsbutton){
            Intent statsScreen = new Intent(this,Stats.class);
            startActivity(statsScreen);
        }
        /* Onclick changes the recorder button between
        play and stop, based on the recorder status.
        Will be updated when recording function is integrated.
         */
        if(view.getId()==R.id.toggleRecording){
            if (SleepMonitor.recorder.isRunning()) {
                // Stop the tracking service
                RecordingService.instance.stopSelf();
                setRecordButton(false);
            } else {
                // Start the tracking service
                Intent trackingIntent = new Intent(MainActivity.this, RecordingService.class);
                MainActivity.this.startService(trackingIntent);
                setRecordButton(true);
            }
        }
    }

    /*set record button to start or stop depending on whether recorder is running or not*/
    private void setRecordButton(boolean running) {
        ImageView button = (ImageView) findViewById(R.id.toggleRecording);
        if (running) {
            button.setImageResource(R.drawable.ic_action_stop);
        } else {
            button.setImageResource(R.drawable.ic_action_play);
        }
    }

    /*SleepMinder code*/
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
