package projects.hobbes.team.reminderapp.puller;

import android.util.Log;

import java.util.List;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.ContactSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class Puller
{
    private static final String TAG = "PullerLog";
    private static Thread puller;

    public static void start()
    {
        if(puller == null)
        {
            puller = new PullerThread();
        }
        puller.start();
        Log.d(TAG, "Passed run");
    }

    public static void stop()
    {
        puller.interrupt();
    }

    public static void refresh()
    {
        synchronized (puller)
        {
            puller.notify();
        }
    }

    private static class PullerThread extends Thread
    {
        private static final int SECOND = 1000;
        private static final int TEN_SECONDS = 10 * SECOND;
        private static final int HALF_MINUTE = 30 * SECOND;
        private static final int MINUTE = 60 * SECOND;
        private static final int TEN_MINUTES = 10 * MINUTE;
        private static final int HALF_HOUR = 30 * MINUTE;
        private static final int HOUR = 60 * MINUTE;

        private int waitTime = HALF_MINUTE;
        private boolean running = true;

        @Override
        public void run()
        {
            while(running)
            {
                try
                {
                    synchronized(this) {
                        wait(waitTime);
                    }
                }
                catch (InterruptedException e)
                {
                    running = false;
                }

                updateReminders();
                Log.d(TAG, "update");
            }
        }

        private void updateReminders() {
            for(String appName : SettingsModel.getInstance().getAppNames()) {
                AppSettings app = SettingsModel.getInstance().getAppSettings(appName);
                if(app.isTurnedOn()) {
                    API api = app.getAPI();
                    List<Reminder> pending = RemindersModel.getInstance().getRemindersList(appName);
                    List<Reminder> messages = api.getMessages();
                    //not sure how to figure out if it is already pending...

                    pending.addAll(messages);
                }
            }
        }
    }
}
