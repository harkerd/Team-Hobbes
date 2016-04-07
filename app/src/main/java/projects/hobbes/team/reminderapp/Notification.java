package projects.hobbes.team.reminderapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import projects.hobbes.team.reminderapp.model.Reminder;

/**
 * Created by Clint on 3/29/2016.
 */
public class Notification {

    private Reminder _reminder;

    public void SendNotification(Context context, Reminder reminder)
    {
        _reminder = reminder;
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.headelephantlight)
//                        .setContentTitle("John Smith")
//                        .setContentText("Knock knock!");
        RemoteViews rv = new RemoteViews("projects.hobbes.team.reminderapp", R.layout.notification_simple);
//        rv.setString(R.id.Name, "setText", "TestName");
        rv.setTextViewText(R.id.Name, reminder.getContactName());
        rv.setTextViewText(R.id.Content, reminder.getMessage());
        //reply should open the app
        //Snooze should snooze the notification (reminder)
        //ignore should turn off reminders for this notification.
//        Intent ignoreIntent = new Intent();
//        rv.setOnClickPendingIntent(R.id.Ignore, );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.headelephantlight)
                        .setContent(rv);
        Intent resultIntent = new Intent(context, MainActivity.class); //Where does this notification lead?

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
