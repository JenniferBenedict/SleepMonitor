package victorine.sleepmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button stats;
    ImageView recorder;
    int recorderStatus = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recorder = (ImageView ) findViewById(R.id.toggleRecording);
        stats = (Button) findViewById(R.id.statsbutton);
        recorder.setOnClickListener(this);
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
            if (recorderStatus ==0){
                recorder.setImageResource(R.drawable.ic_action_stop);
                recorderStatus = 1;
            }
            else{
                recorder.setImageResource(R.drawable.ic_action_play);
                recorderStatus=0;
            }
                        }
        }





    /*will use this to change recorder button from record to stop recording
    once we implement the recording function*/
    private void setRecorderState(int status) {
        ImageView button = (ImageView) findViewById(R.id.toggleRecording);
        if (status ==0) {
            button.setImageResource(R.drawable.ic_action_stop);
            status = 1;
        } else {
            button.setImageResource(R.drawable.ic_action_play);
            status = 0;
        }
    }
}
