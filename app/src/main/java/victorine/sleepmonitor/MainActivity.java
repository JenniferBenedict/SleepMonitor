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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = (ImageView) findViewById(R.id.toggleRecording);
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


    }
    private void setRecorderState(boolean running) {
        ImageView button = (ImageView) findViewById(R.id.toggleRecording);
        if (running) {
            button.setImageResource(R.drawable.ic_action_stop);
        } else {
            button.setImageResource(R.drawable.ic_action_play);
        }
    }
}
