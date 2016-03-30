package projects.hobbes.team.reminderapp.puller;

import android.util.Log;
import java.util.List;
import java.util.Map;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Contact;
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
        private static final int QUARTER_MINUTE = 15 * SECOND;
        private static final int HALF_MINUTE = 30 * SECOND;
        private static final int MINUTE = 60 * SECOND;
        private static final int QUARTER_HOUR = 15 * MINUTE;
        private static final int HALF_HOUR = 30 * MINUTE;
        private static final int HOUR = 60 * MINUTE;

        private int waitTime = QUARTER_MINUTE;
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

        private void updateReminders()
        {
            //*
            for(String appName : SettingsModel.getInstance().getAppNames())
            {
                AppSettings app = SettingsModel.getInstance().getAppSettings(appName);
                if(app.isTurnedOn())
                {
                    API api = app.getAPI();

                    Map<String, Contact> contactsInModel = SettingsModel.getInstance().getAppSettings(appName).getContactMap();
                    List<Contact> contacts = api.getContacts();
                    for(Contact person : contacts) {
                        if(!contactsInModel.containsKey(person.getName())) {
                            contactsInModel.put(person.getName(), person);
                        }
                    }


                    List<Reminder> pending = RemindersModel.getInstance().getRemindersList(appName);
                    List<Reminder> messages = api.getMessages();
                    //TODO: update pending reminders list
                    //Not sure how to figure out if a message is already pending

                    for(Reminder reminder : pending)
                    {
                        if(reminder.isOverdue())
                        {
                            //TODO: ping notifications
                        }
                    }
                    pending.addAll(messages);
                }
            }
            //*/
        }
    }

    /*private static class PullerService extends Service
    {
        private IBinder mBinder;

        @Override
        public void onCreate() {
            // The service is being created
        }

        @Override
        public IBinder onBind(Intent intent) {
            // A client is binding to the service with bindService()
            return mBinder;
        }

        @Override
        public void onDestroy() {
            // The service is no longer used and is being destroyed
        }
    }*/




    public static void populateFakeData()
    {
        SettingsModel.getInstance().addApp("Messenger", new AppSettings());

        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("John Doe", new Contact("John Doe"));
        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("John Smith", new Contact("John Smith"));
        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Jane Doe", new Contact("Jane Doe"));
        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Bosco", new Contact("Bosco"));
        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("James Bond", new Contact("James Bond"));
        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Zoolander", new Contact("Zoolander"));
    }
}
