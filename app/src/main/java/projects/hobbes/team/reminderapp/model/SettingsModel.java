package projects.hobbes.team.reminderapp.model;

import java.util.HashMap;
import java.util.Map;

public class SettingsModel {

    private static SettingsModel instance;
    private final Map<String, AppSettings> appSettingsMap;

    private SettingsModel() {
        appSettingsMap = new HashMap<>();
        appSettingsMap.put("Messenger", new AppSettings());
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



    public String[] getAppNames() {
        return (String[]) appSettingsMap.keySet().toArray();
    }
}
