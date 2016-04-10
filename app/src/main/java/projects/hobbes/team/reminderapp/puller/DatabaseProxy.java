package projects.hobbes.team.reminderapp.puller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import com.google.gson.Gson;


public class DatabaseProxy extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminderDB.sqlite";

    public static final String TABLE_NAME = "Data";
    public static final String SETTINGS_COLUMN_NAME = "settingsJson";
    public static final String REMINDERS_COLUMN_NAME = "remindersJson";

    private static final String SQL_CREATE_DATA = "CREATE TABLE " + TABLE_NAME + " (id INTEGER NOT NULL PRIMARY " +
            "KEY AUTOINCREMENT, " + SETTINGS_COLUMN_NAME + " VARCHAR(255) NOT NULL, " + REMINDERS_COLUMN_NAME + " VARCHAR(255) NOT NULL);";

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

        setData("{}", "{}");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }

    //Singleton
    private static DatabaseProxy instance;
    //private static Gson gson;

    public static void init(Context context) {
        instance = new DatabaseProxy(context);
        //gson = new Gson();
    }

    public static void setData() {
        //setData(gson.toJson(SettingsModel.getInstance()), gson.toJson(RemindersModel.getInstance()));
    }

    private static void setData(String settingsModel, String reminderModel) {
        String command = "INSERT INTO " + TABLE_NAME + " (" + SETTINGS_COLUMN_NAME + ", " + REMINDERS_COLUMN_NAME + ") " +
                "VALUES ('" + settingsModel + "','" + reminderModel + "')";

        SQLiteDatabase db = instance.getWritableDatabase();
        db.execSQL(command);
    }

    public static void getData() {
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = instance.getReadableDatabase();

        String settingsJson = "";
        String remindersJson = "";
        Cursor results = db.rawQuery(query, null);
        if (results.moveToFirst()) {
            settingsJson = results.getString(results.getColumnIndex(SETTINGS_COLUMN_NAME));
            remindersJson = results.getString(results.getColumnIndex(REMINDERS_COLUMN_NAME));
            //SettingsModel.setInstance(gson.fromJson(settingsJson, SettingsModel.class));
            //RemindersModel.setInstance(gson.fromJson(remindersJson, RemindersModel.class));
        }
    }
}

