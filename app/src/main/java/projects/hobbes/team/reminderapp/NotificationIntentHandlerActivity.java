package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

import projects.hobbes.team.reminderapp.model.Reminder;

public class NotificationIntentHandlerActivity extends AppCompatActivity {


    static final String ACTION_REPLY = "project.hobbes.team.reminderapp.action.REPLY";
    static final String ACTION_SNOOZE = "project.hobbes.team.reminderapp.action.SNOOZE";
    static final String ACTION_IGNORE = "project.hobbes.team.reminderapp.action.IGNORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_intent_handler);

        Intent intent = getIntent();
        String Action = intent.getAction();


        String app = intent.getStringExtra("app");
        String name = intent.getStringExtra("name");
        String message = intent.getStringExtra("message");
        Date timeRecieved = new Date(intent.getLongExtra("timeRecieved", -1));

        Reminder reminder = new Reminder(name, app, message, timeRecieved);

        TextView actionText = (TextView) findViewById(R.id.intentActionText);
        actionText.setText(Action + app + name + message + timeRecieved.toString());

        if (Action == ACTION_REPLY){
            doReply(reminder);
        } else if (Action == ACTION_IGNORE){
            doIgnore(reminder);
        } else if (Action == ACTION_SNOOZE){
            doSnooze(reminder);
        }


    }

    private void doReply(Reminder reminder){

    }

    private void doIgnore(Reminder reminder){

    }

    private void doSnooze(Reminder reminder){

    }
}
