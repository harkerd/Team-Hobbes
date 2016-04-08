package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;

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
        //Toast.makeText(this, "reply pressed for " + reminder.getContact().getName(), Toast.LENGTH_SHORT).show();
        SettingsModel.getInstance().getAppSettings(reminder.getApp()).getAPI().launchActivity(reminder.getContact(), this);
        this.finish();
    }

    private void doIgnore(Reminder reminder){
        Toast.makeText(this, "ignore pressed for " + reminder.getContactName(), Toast.LENGTH_SHORT).show();
    }

    private void doSnooze(Reminder reminder){
        Toast.makeText(this, "snooze pressed for " + reminder.getContactName(), Toast.LENGTH_SHORT).show();
    }
}