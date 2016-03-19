package projects.hobbes.team.reminderapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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

    }

}
