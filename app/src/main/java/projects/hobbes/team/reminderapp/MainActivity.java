package projects.hobbes.team.reminderapp;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import projects.hobbes.team.reminderapp.expandableReminderList.AddAppObject;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.model.Thread;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import projects.hobbes.team.reminderapp.expandableReminderList.MyExpandableAdapter;
import projects.hobbes.team.reminderapp.expandableReminderList.MyParentObject;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.puller.Gmail;
import projects.hobbes.team.reminderapp.puller.Puller;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 100;
    static final int REQUEST_AUTHORIZATION = 101;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 102;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 103;
    static final int REQUEST_PERMISSION_READ_CONTACTS = 104;

    private static final String[ ] SCOPES = {GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY, GmailScopes.MAIL_GOOGLE_COM };
    private static final String PREF_ACCOUNT_NAME = "accountName";

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

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        Gmail.credential = mCredential;
        getResultsFromApi();

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


        MyParentObject gmailObject = new MyParentObject("Gmail");
        gmailObject.setChildItemList(remindersModel.getRemindersList("Gmail"));
        parentListItems.add(gmailObject);

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////GMAIL STUFF//////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast toast = Toast.makeText(context, "No network connection available.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //CALL THE API TASK?



        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);

        }
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast toast = Toast.makeText(context, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.",Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }



}
