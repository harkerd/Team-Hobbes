package projects.hobbes.team.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.ContactSettings;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class SettingsActivity extends AppCompatActivity {

    //todo make this better
    private static final String NAME_KEY = "contactName";
    private static final String APP_KEY = "reminderapp.settings.appkey";
    public String contactName;
    public String appName;
    public ContactSettings currentContactSettings;
    public AppSettings currentAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contactName = getIntent().getStringExtra(NAME_KEY);
        appName = "Messenger";


        currentAppSettings = SettingsModel.getInstance().getAppSettings(appName);

        if(contactName != null)
        {
            setTitle(contactName + "'s Settings");
            currentContactSettings = currentAppSettings.getSpecificContactSettings(contactName);

        }
        else {
            setTitle(appName + "'s Default Settings");
        }


        Switch vibrateToggle = (Switch) findViewById(R.id.vibrateToggle);
        if(contactName != null)
        {

            vibrateToggle.setChecked(currentContactSettings.getIsVibrateOn());
            vibrateToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    currentContactSettings.toggleVibrate();
                }
            });

        }
        else
        {
            vibrateToggle.setChecked(currentAppSettings.getDefaultContactSettings().getIsVibrateOn());
            vibrateToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    currentAppSettings.getDefaultContactSettings().toggleVibrate();
                }
            });
        }





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


        if(contactName !=null)
        {
            soundSpinner.setSelection(soundAdapter.getPosition(currentContactSettings.getSound()));
            timeSpinner.setSelection(timeAdapter.getPosition(currentContactSettings.getReminderTime()));
        }
        else
        {
            soundSpinner.setSelection(soundAdapter.getPosition(currentAppSettings.getDefaultContactSettings().getSound()));
            timeSpinner.setSelection(timeAdapter.getPosition(currentAppSettings.getDefaultContactSettings().getReminderTime()));
        }



        soundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(contactName != null)
                {
                    currentContactSettings.setSound(parent.getItemAtPosition(position).toString());
                }
                else
                {
                    currentAppSettings.getDefaultContactSettings().setSound(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if(contactName != null)
                {

                }

            }
        });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(contactName != null)
                {
                    currentContactSettings.setReminderTime(parent.getItemAtPosition(position).toString());
                    RemindersModel.getInstance().updateReminderTimeIfNecessary(appName, contactName);
                }
                else
                {
                    currentAppSettings.getDefaultContactSettings().setReminderTime(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        
        
        
    }

}
