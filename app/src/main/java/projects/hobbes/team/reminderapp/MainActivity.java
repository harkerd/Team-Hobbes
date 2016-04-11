package projects.hobbes.team.reminderapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import projects.hobbes.team.reminderapp.expandableReminderList.AddAppObject;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.hobbes.team.reminderapp.expandableReminderList.MyExpandableAdapter;
import projects.hobbes.team.reminderapp.expandableReminderList.MyParentObject;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.puller.DatabaseProxy;
import projects.hobbes.team.reminderapp.puller.Puller;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    static RecyclerView recyclerView;
    static MyExpandableAdapter expandableAdapter;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        Iconify.with(new FontAwesomeModule());

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Puller.start(this);

        recyclerView = (RecyclerView) findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Notification.setNotificationCount(0);

    }

    private List<ParentListItem> getMessages() {

        RemindersModel remindersModel = RemindersModel.getInstance();
//todo make this get the apps that are in our object, not hard coded
        List<ParentListItem> parentListItems = new ArrayList<>();
        MyParentObject parentObject = new MyParentObject("Messenger");
        parentObject.setChildItemList(remindersModel.getRemindersList("Messenger"));
        parentListItems.add(parentObject);

        parentListItems.add(new AddAppObject());

        return parentListItems;
    }

    public static void refreshIgnoreList(final String appName) {
        if (context == null) {
            return;
        }
        if (recyclerView != null && expandableAdapter != null) {
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "refresh ignore for " + appName);
                    List<Reminder> reminders = RemindersModel.getInstance().getRemindersList(appName);
                    List<Reminder> ignored = RemindersModel.getInstance().getIgnoredReminders(appName);
                    int parentIndex = 0;
                    for (ParentListItem parentListItem : expandableAdapter.getParentItemList()) {
                        if (parentListItem instanceof MyParentObject) {
                            String appNameInList = ((MyParentObject) parentListItem).getTitle();
                            if (appNameInList.equals(appName)) {
                                for (Reminder ignoredReminder : ignored) {
                                    if (reminders.contains(ignoredReminder)) {
                                        int index = parentListItem.getChildItemList().indexOf(ignoredReminder);
                                        RemindersModel.getInstance().getRemindersList(appName).remove(ignoredReminder);
                                        expandableAdapter.notifyChildItemRemoved(parentIndex, index);
                                    }
                                }
                            }
                        }
                        parentIndex++;
                    }
                    expandableAdapter.notifyDataSetChanged();
                    DatabaseProxy.setData();
                }
            });
        }
    }

    public static void refreshList(final Map<String,List<Reminder>> addedMessages, final Map<String,List<Reminder>> removedMessages) {
        if (context == null || !((MainActivity)context).hasWindowFocus()) {
            return;
        }
        if (recyclerView != null && expandableAdapter != null) {
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int size = RemindersModel.getInstance().getRemindersList("Messenger").size();
                    Log.d(TAG, "notifying data set changed. current size: " + size);

                    // this stuff is purely for debug -------------------------------
                    int addSize = 0;
                    for (String key : addedMessages.keySet()) {
                        addSize += addedMessages.get(key).size();
                    }
                    Log.d(TAG, "notifying data set changed. add size: " + addSize);
                    int removeSize = 0;
                    for (String key : removedMessages.keySet()) {
                        removeSize += removedMessages.get(key).size();
                    }
                    Log.d(TAG, "notifying data set changed. remove size: " + removeSize);
                    // ---------------------------------------------------------------

                    // add messages that need to be added
                    if (addedMessages.size() > 0) {
                        for (String app : addedMessages.keySet()) {
                            for (ParentListItem parentListItem : expandableAdapter.getParentItemList()) {
                                if (parentListItem instanceof MyParentObject) {
                                    String appName = ((MyParentObject) parentListItem).getTitle();
                                    if (app.equals(appName)) {
                                        RemindersModel.sort(addedMessages.get(app));
                                        for (int i = addedMessages.get(app).size() -1; i >= 0 ; i--) {
                                            RemindersModel.getInstance().getRemindersList(app).add(0, addedMessages.get(app).get(i));
                                            expandableAdapter.notifyChildItemInserted(0, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //remove messages that need to be removed
                    if (removedMessages.size() > 0) {
                        for (String app : removedMessages.keySet()) {
                            for (ParentListItem parentListItem : expandableAdapter.getParentItemList()) {
                                if (parentListItem instanceof MyParentObject) {
                                    String appName = ((MyParentObject) parentListItem).getTitle();
                                    if (app.equals(appName)) {
                                        for (int i = 0; i < removedMessages.get(app).size(); i++) {
                                            int index = parentListItem.getChildItemList().indexOf(removedMessages.get(app).get(i));
                                            RemindersModel.getInstance().getRemindersList(app).remove(removedMessages.get(app).get(i));
                                            expandableAdapter.notifyChildItemRemoved(0, index);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    expandableAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Puller.refresh();
        Log.d(TAG, "onResume after refresh called");
        refreshReminders();
    }

    private static void refreshReminders() {
        expandableAdapter = new MyExpandableAdapter(context, ((MainActivity)context).getMessages());
        recyclerView.setAdapter(expandableAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        super.onPause();
//        Reminder r = new Reminder("Test Name", "Messenger", "Text of long message", new Date(System.currentTimeMillis()), 7);
//        Notification n = new Notification();
//        n.SendNotification(this, r);
    }

    public static void sendNotification(Reminder reminder) {
        Notification n = new Notification();
        n.SendNotification(context, reminder);
    }

//    public void SendNotification()
//    {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.headelephantlight)
//                        .setContentTitle("John Smith")
//                        .setContentText("Knock knock!");
//
//        NotificationCompat.InboxStyle inboxStyle =
//                new NotificationCompat.InboxStyle();
//        String[] events = new String[6];
//// Sets a title for the Inbox in expanded layout
//        inboxStyle.setBigContentTitle("Event tracker details:");
//// Moves events into the expanded layout
//        for (int i = 0; i < events.length; i++)
//        {
//            events[i] = "test";
//            inboxStyle.addLine(events[i]);
//        }
//// Moves the expanded layout object into the notification object.
//       // mBuilder.setStyle(inboxStyle);
//// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(0, mBuilder.build());
//    }

    @Override
    public void onStart()
    {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://projects.hobbes.team.reminderapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://projects.hobbes.team.reminderapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
