package projects.hobbes.team.reminderapp.puller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;

import projects.hobbes.team.reminderapp.messenger.Messenger;
import projects.hobbes.team.reminderapp.model.AppSettings;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.RemindersModel;
import projects.hobbes.team.reminderapp.model.SettingsModel;


public class DatabaseProxy extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminderDB.sqlite";

    public static final String TABLE_NAME = "Data";
    public static final String ID_COLUMN_NAME = "id";
    public static final String ID_NUMBER = "0";
    public static final String SETTINGS_COLUMN_NAME = "settingsJson";
    public static final String REMINDERS_COLUMN_NAME = "remindersJson";

    private static final String SQL_CREATE_DATA = "CREATE TABLE " + TABLE_NAME + " (" + ID_COLUMN_NAME + " INTEGER NOT NULL PRIMARY " +
            "KEY, " + SETTINGS_COLUMN_NAME + " VARCHAR(255) NOT NULL, " + REMINDERS_COLUMN_NAME + " VARCHAR(255) NOT NULL);";

    private static final String SQL_DELETE_DATA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    private DatabaseProxy(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();
        db.execSQL(SQL_DELETE_DATA);
        db.execSQL(SQL_CREATE_DATA);
        db.setTransactionSuccessful();
        db.endTransaction();
        createAPIs();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }

    //Singleton
    private static DatabaseProxy instance;
    private static Gson gson;

    public static void init(Context context) {
        instance = new DatabaseProxy(context);
        gson = new Gson();
    }

    public static void setData() {
        setData(gson.toJson(SettingsModel.getInstance()), gson.toJson(RemindersModel.getInstance()));
    }

    private static void setData(String settingsModel, String reminderModel) {
        String command = "INSERT OR REPLACE INTO " + TABLE_NAME + " (" + ID_COLUMN_NAME + "," + SETTINGS_COLUMN_NAME + ", " + REMINDERS_COLUMN_NAME + ") " +
                "VALUES (" + ID_NUMBER + ",'" + settingsModel + "','" + reminderModel + "')";

        SQLiteDatabase db = instance.getWritableDatabase();
        db.execSQL(command);
    }

    public static void getData() {
        if(SettingsModel.getInstance().getAppNames().size() == 0)
        {
            createAPIs();
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + "=" + ID_NUMBER + ";";
        SQLiteDatabase db = instance.getReadableDatabase();

        String settingsJson = "";
        String remindersJson = "";
        Cursor results = db.rawQuery(query, null);
        if (results.moveToLast()) {
            settingsJson = results.getString(results.getColumnIndex(SETTINGS_COLUMN_NAME));
            remindersJson = results.getString(results.getColumnIndex(REMINDERS_COLUMN_NAME));
            SettingsModel.setInstance(gson.fromJson(settingsJson, SettingsModel.class));
            RemindersModel.setInstance(gson.fromJson(remindersJson, RemindersModel.class));
        }
        results.close();
    }

    public static void createAPIs()
    {
        SettingsModel.getInstance().addApp("Messenger", new AppSettings("Messenger"));
        RemindersModel.getInstance().addApp("Messenger", new ArrayList<Reminder>());
    }
}

