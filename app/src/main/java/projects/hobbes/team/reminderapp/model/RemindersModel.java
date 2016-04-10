package projects.hobbes.team.reminderapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import projects.hobbes.team.reminderapp.puller.Puller;
import projects.hobbes.team.reminderapp.puller.PullerThread;

/**
 * Created by Cory on 3/15/2016.
 */
public class RemindersModel {

    private transient static RemindersModel instance;

    private Map<String, List<Reminder>> remindersList;
    private Map<String, List<Reminder>> ignoredRemindersList;

    private RemindersModel() {
        remindersList = new ConcurrentHashMap<>();
        ignoredRemindersList = new ConcurrentHashMap<>();
    }

    public static RemindersModel getInstance() {
        if (instance == null) {
            instance = new RemindersModel();
        }
        return instance;
    }

    public static void setInstance(RemindersModel model) {
        instance = model;
    }

    public void addApp(String appName, List<Reminder> reminders) {
        remindersList.put(appName, reminders);
        ignoredRemindersList.put(appName, new ArrayList<Reminder>());
    }

    public List<Reminder> getRemindersList(String appName) {
        return remindersList.get(appName);
    }

    public void clearRemindersList(String appName) {
        List<Reminder> reminders = this.remindersList.get(appName);
        if (reminders != null) {
            reminders.clear();
        }
    }

    public void updateReminderTimeIfNecessary(String appName, String contactName) {
        for (Reminder reminder : remindersList.get(appName)) {
            if (reminder.getContactName().equals(contactName)) {
                Contact contact = SettingsModel.getInstance().getAppSettings(appName).getContactMap().get(contactName);
                String reminderTime = contact.getContactSettings().getReminderTime();
                Date remindTime = new Date(reminder.getTimeReceived().getTime() + PullerThread.stringToMilSeconds(reminderTime));
                //update message
                reminder.updateData(contact, remindTime);
            }
        }
    }

    public static void sort(List<Reminder> list) {
        Collections.sort(list, new Comparator<Reminder>() {
            public int compare(Reminder reminder1, Reminder reminder2) {
                return Integer.compare(reminder1.getTimeSinceReceived(), reminder2.getTimeSinceReceived());
            }
        });
    }

    public List<Reminder> getIgnoredReminders(String appName) {
        return ignoredRemindersList.get(appName);
    }

    public Map<String, List<Reminder>> getIgnoredRemindersList() {
        return ignoredRemindersList;
    }
}
