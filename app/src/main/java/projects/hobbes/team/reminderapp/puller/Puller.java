package projects.hobbes.team.reminderapp.puller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import projects.hobbes.team.reminderapp.messenger.Messenger;
import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;

public class Puller
{
    private static final String TAG = "PullerLog";
    private static Thread puller;
    private static List<InitialDataLoadingListener> listeners;

    protected static Context context;
    protected static boolean loadingInitialData = true;

    public static void start(Context context)
    {
        Puller.context = context;
        if(puller == null)
        {
            puller = new PullerThread();
            DatabaseProxy.init(context);
            DatabaseProxy.getData();
        }
        if ( !((PullerThread)puller).isRunning() ) {
            Log.d(TAG, "Starting the puller");
            puller.start();
        }
    }

    public static void stop()
    {
        puller.interrupt();
    }

    public static void refresh()
    {
        synchronized (puller)
        {
            puller.notify();
        }
    }

    public static void setLoadingInitialDataListener(InitialDataLoadingListener listener) {
        if(listeners == null)
        {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    protected static void notifyListeners() {
        if(listeners != null)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                listeners.get(i).initialDataLoaded();
            }
        }
    }

    public static boolean isLoading() {
        return loadingInitialData;
    }

    public interface InitialDataLoadingListener {
        void initialDataLoaded();
    }
}
