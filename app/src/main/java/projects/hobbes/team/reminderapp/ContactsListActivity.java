package projects.hobbes.team.reminderapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.ContactsListAdapter;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class ContactsListActivity extends AppCompatActivity {

    private ContactsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView listView = (RecyclerView) findViewById(R.id.contact_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        String appName = getIntent().getStringExtra("AppName");
        AppSettings appSettings = SettingsModel.getInstance().getAppSettings(appName);

        adapter = new ContactsListAdapter(this, appSettings.getContactMap());
        listView.setAdapter(adapter);
    }

}
