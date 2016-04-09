package projects.hobbes.team.reminderapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.ContactsListAdapter;
import projects.hobbes.team.reminderapp.model.SettingsModel;
import projects.hobbes.team.reminderapp.puller.Puller;

public class ContactsListActivity extends AppCompatActivity implements Puller.InitialDataLoadingListener{

    private ContactsListAdapter adapter;
    private ProgressBar spinner;
    private String appName;
    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView listView = (RecyclerView) findViewById(R.id.contact_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        appName = getIntent().getStringExtra("AppName");
        appSettings = SettingsModel.getInstance().getAppSettings(appName);

        adapter = new ContactsListAdapter(this, appSettings.getContactMap(), appName);
        listView.setAdapter(adapter);


        if(Puller.isLoading())
        {
            spinner = (ProgressBar) findViewById(R.id.progressBar);
            spinner.setVisibility(View.VISIBLE);
            Puller.setLoadingInitialDataListener(this);
        }
    }

    @Override
    public void initialDataLoaded()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                spinner.setVisibility(View.GONE);
                adapter.setData(appSettings.getContactMap(), appName);
            }
        });
    }
}
