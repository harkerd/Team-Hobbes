package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;
import projects.hobbes.team.reminderapp.puller.FakeMessenger;

public class AppSettingsActivity extends AppCompatActivity {

    public String appName;
    public AppSettings currentAppSettings;

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
            SettingsModel.getInstance().addApp(appName, new AppSettings(appName));
            RemindersModel.getInstance().addApp(appName, new ArrayList<Reminder>());
        }

        currentAppSettings = SettingsModel.getInstance().getAppSettings(appName);

        Switch vibrateToggle = (Switch) findViewById(R.id.vibrateToggle);

        vibrateToggle.setChecked(currentAppSettings.getDefaultContactSettings().getIsVibrateOn());
        vibrateToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                currentAppSettings.getDefaultContactSettings().toggleVibrate();
            }
        });



        RelativeLayout contactButton = (RelativeLayout) findViewById(R.id.contactSettingsButton);

        final Spinner soundSpinner = (Spinner) findViewById(R.id.soundSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> soundAdapter = ArrayAdapter.createFromResource(this,
                R.array.sounds_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to thesoundSpinner
        soundSpinner.setAdapter(soundAdapter);

        Spinner timeSpinner = (Spinner) findViewById(R.id.timesSpinner);
        // Create an ArrayAdapter using the string array and a default timeSpinner layout
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the timeAdapter to the timeSpinner
        timeSpinner.setAdapter(timeAdapter);
        currentAppSettings = SettingsModel.getInstance().getAppSettings(appName);

        soundSpinner.setSelection(soundAdapter.getPosition(currentAppSettings.getDefaultContactSettings().getSound()));
        timeSpinner.setSelection(timeAdapter.getPosition(currentAppSettings.getDefaultContactSettings().getReminderTime()));




        soundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                currentAppSettings.getDefaultContactSettings().setSound(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {


            }
        });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                currentAppSettings.getDefaultContactSettings().setReminderTime(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        contactButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), ContactsListActivity.class);
                intent.putExtra("AppName", appName);
                startActivity(intent);

            }
        });
    }

}
