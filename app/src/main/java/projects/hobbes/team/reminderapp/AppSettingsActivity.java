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

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class AppSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "1");
        super.onCreate(savedInstanceState);
        Log.d("tag", "2");
        setContentView(R.layout.activity_app_settings);
        Log.d("tag", "3");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Log.d("tag", "4");
        setSupportActionBar(toolbar);
        Log.d("tag", "5");

        if(SettingsModel.getInstance().getAppSettings("Messenger") == null)
        {
            SettingsModel.getInstance().addApp("Messenger", new AppSettings());
        }

        Button contactButton = (Button) findViewById(R.id.contactSettingsButton);
        Button defaultButton = (Button) findViewById(R.id.defaultSettingsButton);

        defaultButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), ContactsListActivity.class);
                intent.putExtra("AppName", "Messenger");
                startActivity(intent);

            }
        });
    }

}
