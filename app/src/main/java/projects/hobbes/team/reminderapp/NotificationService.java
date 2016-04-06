package projects.hobbes.team.reminderapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
    public NotificationService() {
    }

    NotificationAlarm alarm = new NotificationAlarm();
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        alarm.SetAlarm(this);
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
