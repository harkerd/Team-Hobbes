package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.ArrayList;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;
import projects.hobbes.team.reminderapp.puller.FakeMessenger;

public class AppSettingsActivity extends AppCompatActivity {

    public String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appName = "Messenger";
        Log.d("tag", "1");
        super.onCreate(savedInstanceState);
        Log.d("tag", "2");
        setContentView(R.layout.activity_app_settings);
        Log.d("tag", "3");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Log.d("tag", "4");
        setSupportActionBar(toolbar);
        Log.d("tag", "5");

        setTitle(appName + " Settings");

        Switch appsOnOrOffToggle = (Switch) findViewById(R.id.appRemindersToggle);
        appsOnOrOffToggle.setChecked(SettingsModel.getInstance().getAppSettings(appName).isTurnedOn());
        appsOnOrOffToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsModel.getInstance().getAppSettings(appName).toggleIsTurnedOn();
                if (!SettingsModel.getInstance().getAppSettings(appName).isTurnedOn()) {
                    RemindersModel.getInstance().clearRemindersList(appName);
                }
            }
        });


        if(SettingsModel.getInstance().getAppSettings(appName) == null)
        {
            SettingsModel.getInstance().addApp(appName, new AppSettings(new FakeMessenger()));
        }

        RelativeLayout contactButton = (RelativeLayout) findViewById(R.id.contactSettingsButton);
        RelativeLayout defaultButton = (RelativeLayout) findViewById(R.id.defaultSettingsButton);

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ContactsListActivity.class);
                intent.putExtra("AppName", appName);
                startActivity(intent);

            }
        });
    }

}
