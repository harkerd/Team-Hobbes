package projects.hobbes.team.reminderapp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import projects.hobbes.team.reminderapp.puller.FakeMessenger;

public class SettingsModel {

    private transient static SettingsModel instance;
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

    public static void setInstance(SettingsModel model) {
        instance = model;
    }

    public void addApp(String appName, AppSettings appSettings) {
        appSettingsMap.put(appName, appSettings);
    }

    public AppSettings getAppSettings(String appName) {
        return appSettingsMap.get(appName);
    }

    public Set<String> getAppNames() {
        return appSettingsMap.keySet();
    }
}
