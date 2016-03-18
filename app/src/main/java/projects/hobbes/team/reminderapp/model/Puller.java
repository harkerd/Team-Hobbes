package projects.hobbes.team.reminderapp.model;

import android.util.Log;

public class Puller
{
    private static final String TAG = "PullerLog";
    private static boolean running = false;

    public static void start()
    {
        if(running)
        {
            Log.e(TAG, "Start called while Puller is already running");
        }
        else
        {
            running = !running;
        }
    }

    public static void stop()
    {
        if(!running)
        {
            Log.e(TAG, "Stop called while Puller is not running");
        }
        else
        {
            running = !running;
        }
    }

    private static void run()
    {
        //TODO: implement Puller run()
    }
}
