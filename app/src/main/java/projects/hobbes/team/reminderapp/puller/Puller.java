package projects.hobbes.team.reminderapp.puller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import projects.hobbes.team.reminderapp.MainActivity;
import projects.hobbes.team.reminderapp.messenger.Messenger;
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
    private static Context context;

    public static void start(Context context)
    {
        Puller.context = context;
        if(puller == null)
        {
            puller = new PullerThread();
        }
        if ( !((PullerThread)puller).isRunning() ) {
            //populateFakeData();
            puller.start();
        }
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

    private static final int SECOND = 1000;
    private static final int QUARTER_MINUTE = 15 * SECOND;
    private static final int HALF_MINUTE = 30 * SECOND;
    private static final int MINUTE = 60 * SECOND;
    private static final int QUARTER_HOUR = 15 * MINUTE;
    private static final int HALF_HOUR = 30 * MINUTE;
    private static final int HOUR = 60 * MINUTE;

    public static int stringToMilSeconds(String time) {
        int milSeconds = 0;

        switch (time) {
            case "15 Min": milSeconds = QUARTER_HOUR; break;
            case "30 Min": milSeconds = HALF_HOUR; break;
            case "45 Min": milSeconds = QUARTER_HOUR + HALF_HOUR; break;
            case "1 hour": milSeconds = HOUR; break;
        }

        return milSeconds;
    }

    private static class PullerThread extends Thread
    {
        private int waitTime = QUARTER_MINUTE;
        private boolean running = false;

        public boolean isRunning() {
            return running;
        }

        @Override
        public void start() {
            super.start();
            running = true;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
        }

        @Override
        public void run()
        {
            while(running)
            {
                updateReminders();
                Log.d(TAG, "update");
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
            }
        }

        private void updateReminders()
        {
            for(String appName : SettingsModel.getInstance().getAppNames())
            {
                AppSettings app = SettingsModel.getInstance().getAppSettings(appName);
                if(app.isTurnedOn())
                {
                    API api = app.getAPI();

                    Map<String, Contact> contactsForModel = new HashMap<>();
                    List<Contact> contacts = api.getContacts(Puller.context);
                    for(Contact person : contacts) {
                        contactsForModel.put(person.getName(), person);
                    }
                    SettingsModel.getInstance().getAppSettings(appName).setContacts(contactsForModel);

                    //merge lists
                    List<Reminder> pendingMessagesInModel = RemindersModel.getInstance().getRemindersList(appName);
                    List<Reminder> messagesFromAPI = api.getMessages(Puller.context);

                    List<Reminder> messagesToAdd = new ArrayList<>();
                    for(Reminder message : messagesFromAPI)
                    {
                        //find the index if it is in the model already
                        int index = pendingMessagesInModel.indexOf(message);
                        if(index != -1) //if it is NOT in the model already
                        {
                            message = pendingMessagesInModel.get(index);
                            String contactName = message.getContactName();
                            Contact contact = null;
                            if(contactsForModel.containsKey(contactName))
                            {
                                contact = contactsForModel.get(contactName);
                            }
                            else
                            {
                                ContactSettings defaultSettings = SettingsModel.getInstance().getAppSettings(appName).getDefaultContactSettings();
                                contact = new Contact(defaultSettings, contactName, null);
                            }
                            //update message
                            message.updateData(contact, null);
                        }
                        else //if it IS in the model already
                        {
                            String contactName = message.getContactName();
                            Contact contact = contactsForModel.get(contactName);
                            String reminderTime = contact.getContactSettings().getReminderTime();
                            Date remindTime = new Date(message.getTimeReceived().getTime() + stringToMilSeconds(reminderTime));
                            //update message
                            message.updateData(contact, remindTime);
                            messagesToAdd.add(message);
                        }
                    }

                    pendingMessagesInModel.addAll(messagesToAdd);
                    for(Reminder reminder : pendingMessagesInModel)
                    {
                        if(reminder.isOverdue())
                        {
                            MainActivity.sendNotification(reminder);
                        }
                    }
                }
            }
            MainActivity.refreshList();
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
        API messenger = new Messenger();
        SettingsModel.getInstance().addApp("Messenger", new AppSettings(messenger));
        RemindersModel.getInstance().addApp("Messenger", new ArrayList<Reminder>());

//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("John Doe", new Contact("John Doe"));
//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("John Smith", new Contact("John Smith"));
//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Jane Doe", new Contact("Jane Doe"));
//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Bosco", new Contact("Bosco"));
//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("James Bond", new Contact("James Bond"));
//        SettingsModel.getInstance().getAppSettings("Messenger").getContactMap().put("Zoolander", new Contact("Zoolander"));
    }
}
