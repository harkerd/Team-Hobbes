package projects.hobbes.team.reminderapp.puller;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import projects.hobbes.team.reminderapp.MainActivity;
import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.ContactSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class PullerThread extends Thread
{
    private static final String TAG = "PullerThreadLog";

    private static final int SECOND = 1000;
    private static final int QUARTER_MINUTE = 15 * SECOND;
    private static final int HALF_MINUTE = 30 * SECOND;
    private static final int MINUTE = 60 * SECOND;
    private static final int QUARTER_HOUR = 15 * MINUTE;
    private static final int HALF_HOUR = 30 * MINUTE;
    private static final int HOUR = 60 * MINUTE;

    private int waitTime = QUARTER_MINUTE;
    private boolean running = false;


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

    public boolean isRunning() {
        return running;
    }

    @Override
    public void start() {
        super.start();
        running = true;
        Log.d(TAG, "Done starting");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        running = false;
    }

    @Override
    public void run()
    {
        Log.d(TAG, "Starting to run");
        while(running)
        {
            Log.d(TAG, "starting update");
            updateData();
            if(Puller.loadingInitialData) {
                Puller.loadingInitialData = false;
                Puller.notifyListeners();
            }
            Log.d(TAG, "update complete");
            try
            {
                Log.d(TAG, "Starting wait");
                synchronized(this) {
                    wait(waitTime);
                }
                Log.d(TAG, "Done waiting");
            }
            catch (InterruptedException e)
            {
                running = false;
            }
        }
    }

    private void updateData()
    {
        Map<String,List<Reminder>> messagesToAddToApps = new HashMap<>();
        Map<String,List<Reminder>> messagesToRemoveFromApps = new HashMap<>();
        for(String appName : SettingsModel.getInstance().getAppNames())
        {
            AppSettings app = SettingsModel.getInstance().getAppSettings(appName);
            if(app.isTurnedOn())
            {
                API api = app.getAPI();

                Map<String, Contact> contactsForModel = new HashMap<>();
                List<Contact> contacts = api.getContacts(Puller.context);
                for(Contact person : contacts) {
                    Contact contact = app.getContact(person.getName());
                    if(contact != null) {
                        person.setContactSettings(contact.getContactSettings());
                    }
                    contactsForModel.put(person.getName(), person);
                }
                SettingsModel.getInstance().getAppSettings(appName).setContacts(contactsForModel);

                //merge lists
                List<Reminder> pendingMessagesInModel = RemindersModel.getInstance().getRemindersList(appName);
                List<Reminder> messagesFromAPI = api.getMessages(Puller.context);
                if (messagesFromAPI == null) {
                    continue;
                }
                List<Reminder> messagesToAdd = new ArrayList<>();
                List<Reminder> newMessages = new ArrayList<>();
                for(Reminder message : messagesFromAPI)
                {
                    //find the index if it is in the model already
                    int indexInModel = indexOfReminder(pendingMessagesInModel, message);
                    int indexInIgnored = indexOfReminder(RemindersModel.getInstance().getIgnoredReminders(appName), message);
                    if(indexInModel != -1 || indexInIgnored != -1) //if it is IS in the model or ignored already
                    {
                        if (indexInIgnored != -1) {
                            message = RemindersModel.getInstance().getIgnoredReminders(appName).get(indexInIgnored);
                        }
                        else {
                            message = pendingMessagesInModel.get(indexInModel);
                        }
//                            String contactName = message.getContactName();
                        Contact contact = message.getContact();
                        //update message
                        message.updateData(contact, null);
                        newMessages.add(message);
                    }
                    else //if it NOT in the model or ignored already
                    {
                        String contactName = message.getContactName();
                        Contact realContact = null;
                        for (Contact contact : contactsForModel.values()) {
                            if (contact.getContactInfo().contains(contactName)) {
                                realContact = contact;
                                message.setContactName(realContact.getName());
                            }
                        }

                        if(realContact == null)
                        {
                            ContactSettings defaultSettings = SettingsModel.getInstance().getAppSettings(appName).getDefaultContactSettings();
                            realContact = new Contact(defaultSettings, contactName, Collections.singletonList(contactName));
                        }
                        String reminderTime = realContact.getContactSettings().getReminderTime();
                        Date remindTime = new Date(message.getTimeReceived().getTime() + stringToMilSeconds(reminderTime));
                        //update message
                        message.updateData(realContact, remindTime);
                        messagesToAdd.add(message);
                        newMessages.add(message);
                    }
                }

                messagesToAddToApps.put(appName, messagesToAdd);
                List<Reminder> messagesToRemove = new ArrayList<>();
                for(Reminder reminder : pendingMessagesInModel)
                {
                    //if message has been responded to, it will no longer be in the pulled in messages, but will still
                    //be in the message in the app. Need to remove those
                    //todo do something similar for ignored messages
                    if (indexOfReminder(newMessages, reminder) == -1) {
                        messagesToRemove.add(reminder);
                    }
                    else if(reminder.isOverdue() && !reminder.isNotificationSent())
                    {
                        MainActivity.sendNotification(reminder);
                        reminder.setNotificationSent(true);
                    }
                }
                if (messagesToRemove.size() > 0) {
                    messagesToRemoveFromApps.put(appName, messagesToRemove);
                }
            }
        }
        MainActivity.refreshList(messagesToAddToApps, messagesToRemoveFromApps);
        DatabaseProxy.setData();
    }

    private int indexOfReminder(List<Reminder> reminderList, Reminder reminder)
    {
        for (int i = 0; i < reminderList.size(); i++)
        {
            Reminder r = reminderList.get(i);
            if (r.getContactName().equals(reminder.getContactName())
                    || r.getContact().getContactInfo().contains(reminder.getContactName()))
            {
                if (r.getMessage().equals(reminder.getMessage()))
                {
                    if (r.getTimeReceived().equals(reminder.getTimeReceived()))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}

