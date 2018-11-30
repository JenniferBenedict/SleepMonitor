package victorine.sleepmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button stats;
    ImageView recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordButton = (ImageView ) findViewById(R.id.toggleRecording);
        stats = (Button) findViewById(R.id.statsbutton);

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
}
