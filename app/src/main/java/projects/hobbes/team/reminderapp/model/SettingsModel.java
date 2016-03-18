package projects.hobbes.team.reminderapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cory on 3/15/2016.
 */
public class SettingsModel {

    private static SettingsModel instance;
    private final Map<String, AppSettings> appSettingsMap;

    private SettingsModel() {
        appSettingsMap = new HashMap<>();
    }

    public static SettingsModel getInstance() {
        if (instance == null) {
            instance = new SettingsModel();
        }
        return instance;
    }

    public void addApp(String appName, AppSettings appSettings) {
        appSettingsMap.put(appName, appSettings);
    }

    public AppSettings getAppSettings(String appName) {
        return appSettingsMap.get(appName);
    }
}
