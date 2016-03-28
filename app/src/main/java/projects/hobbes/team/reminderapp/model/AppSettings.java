package projects.hobbes.team.reminderapp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import projects.hobbes.team.reminderapp.puller.API;

public class AppSettings {

    private boolean isTurnedOn = true;
    private ContactSettings defaultContactSettings;
    private Map<String, Contact> contactMap;
    private API api;
    //private String pollingIntervals???

    public AppSettings() {
        defaultContactSettings = new ContactSettings();
        contactMap = new TreeMap<>();
        contactMap.put("John Doe", new Contact("John Doe"));
        contactMap.put("John Smith", new Contact("John Smith"));
        contactMap.put("Jane Doe", new Contact("Jane Doe"));
        contactMap.put("Bosco", new Contact("Bosco"));
        contactMap.put("James Bond", new Contact("James Bond"));
        contactMap.put("Zoolander", new Contact("Zoolander"));
    }

    public ContactSettings getDefaultContactSettings() {
        return defaultContactSettings;
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

    public API getAPI() {
        return api;
    }
}
