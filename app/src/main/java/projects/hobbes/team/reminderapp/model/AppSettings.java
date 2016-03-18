package projects.hobbes.team.reminderapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cory on 3/15/2016.
 */
public class AppSettings {

    private boolean isTurnedOn = true;
    private Settings defaultSettings;
    private Map<String, Contact> contactMap;
    //private String pollingIntervals???

    public AppSettings() {
        defaultSettings = new Settings();
        contactMap = new HashMap<>();
    }

    public Settings getDefaultSettings() {
        return defaultSettings;
    }

    public Map<String, Contact> getContactMap() {
        return contactMap;
    }

    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    public void toggleIsTurnedOn() {
        isTurnedOn = !isTurnedOn;
    }
}
