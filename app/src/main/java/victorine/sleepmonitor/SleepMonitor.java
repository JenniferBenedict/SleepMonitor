package victorine.sleepmonitor;

import android.app.Application;
import android.content.Context;

import de.sopamo.uni.sleepminder.lib.Recorder;

/*SleepMinder code*/
public class SleepMonitor extends Application {
    public static Context context;
    public static Recorder recorder;

    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;

        recorder = new Recorder();
    }
}
