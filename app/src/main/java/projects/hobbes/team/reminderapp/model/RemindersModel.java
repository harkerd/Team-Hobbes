package projects.hobbes.team.reminderapp.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.hobbes.team.reminderapp.puller.Puller;

/**
 * Created by Cory on 3/15/2016.
 */
public class RemindersModel {

    private static RemindersModel instance;

    private Map<String, List<Reminder>> remindersList;

    private RemindersModel() {
        remindersList = new HashMap<>();
    }

    public static RemindersModel getInstance() {
        if (instance == null) {
            instance = new RemindersModel();
        }
        return instance;
    }

    public void addApp(String appName, List<Reminder> reminders) {
        remindersList.put(appName, reminders);
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
                Date remindTime = new Date(reminder.getTimeReceived().getTime() + Puller.stringToMilSeconds(reminderTime));
                //update message
                reminder.updateData(contact, remindTime);
            }
        }
    }
}
