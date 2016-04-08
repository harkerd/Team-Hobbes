package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;
import projects.hobbes.team.reminderapp.puller.Puller;

public class NotificationIntentHandlerActivity extends AppCompatActivity {


    static final String ACTION_REPLY = "project.hobbes.team.reminderapp.action.REPLY";
    static final String ACTION_SNOOZE = "project.hobbes.team.reminderapp.action.SNOOZE";
    static final String ACTION_IGNORE = "project.hobbes.team.reminderapp.action.IGNORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_intent_handler);

        TextView actionText = (TextView) findViewById(R.id.intentActionText);

        Intent intent = getIntent();
        String Action = intent.getAction();
        
        String app = intent.getStringExtra("app");
        String name = intent.getStringExtra("name");
        String message = intent.getStringExtra("message");
        Date timeRecieved = new Date(intent.getLongExtra("timeRecieved", -1));

        Reminder reminder = findReminder(app, name, message, timeRecieved);

        actionText.setText(Action + app + name + message + timeRecieved.toString());
        if (reminder == null) {
            actionText.setText("could not find the reminder requested");
        }

        switch (Action) {
            case ACTION_REPLY:
                doReply(reminder);
                break;
            case ACTION_IGNORE:
                doIgnore(reminder);
                break;
            case ACTION_SNOOZE:
                doSnooze(reminder);
                break;
        }


    }

    private Reminder findReminder(String app, String contactName, String message, Date timeRecieved) {
        Reminder r = new Reminder(contactName, app, message, timeRecieved);
        for (Reminder reminder : RemindersModel.getInstance().getRemindersList(app)) {
            if (reminder.equals(r)) {
                return reminder;
            }
        }
        return null;
    }

    private void doReply(Reminder reminder){
       SettingsModel.getInstance().getAppSettings(reminder.getApp()).getAPI().launchActivity(reminder.getContact(), this);
        this.finish();
    }

    private void doIgnore(Reminder reminder){
        reminder.setIgnore(true);
        RemindersModel.getInstance().getIgnoredReminders(reminder.getApp()).add(reminder);
        MainActivity.refreshIgnoreList(reminder.getApp());
        this.finish();
    }

    private void doSnooze(Reminder reminder){
        String reminderTime = reminder.getContact().getContactSettings().getReminderTime();
        Date remindTime = new Date(new Date().getTime() + Puller.stringToMilSeconds(reminderTime));
        reminder.updateData(reminder.getContact(), remindTime);
        reminder.setNotificationSent(false);
        MainActivity.refreshList(new HashMap<String, List<Reminder>>(), new HashMap<String, List<Reminder>>());
        this.finish();
    }
}
