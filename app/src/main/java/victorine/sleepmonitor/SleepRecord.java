package victorine.sleepmonitor;

public class SleepRecord {
    private int day;  // 0=Sunday, 1=Monday, etc.
    private String date; // format of mm/dd
    private float total_sleep;
    private double light_sleep; // percentage of total sleep
    private double deep_sleep; //percentage of total sleep

    public SleepRecord() {

    }

    public SleepRecord(int day, String date, int total_sleep, double light_sleep, double deep_sleep) {
        this.day = day;
        this.date = date;
        this.total_sleep = total_sleep;
        this.light_sleep = light_sleep;
        this.deep_sleep = deep_sleep;
    }

    public int getDay() {
        return day;
    }

    public double getDeepSleep() {
        return deep_sleep;
    }

    public double getLightSleep() {
        return light_sleep;
    }

    public float getTotalSleep() {
        return total_sleep;
    }

    public String getDate() {
        return date;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDeepSleep(double deep_sleep) {
        this.deep_sleep = deep_sleep;
    }

    public void setLightSleep(double light_sleep) {
        this.light_sleep = light_sleep;
    }

    public void setTotalSleep(float total_sleep) {
        this.total_sleep = total_sleep;
    }
}
