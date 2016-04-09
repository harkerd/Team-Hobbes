package projects.hobbes.team.reminderapp.model;

import java.util.Map;
import java.util.TreeMap;

import projects.hobbes.team.reminderapp.puller.API;

public class AppSettings {

    private boolean isTurnedOn = true;
    private ContactSettings defaultContactSettings;
    private Map<String, Contact> contactMap;
    private API api;

    public AppSettings(API api) {
        defaultContactSettings = new ContactSettings();
        contactMap = new TreeMap<>();
        this.api = api;
    }

    public ContactSettings getDefaultContactSettings() {
        return defaultContactSettings;
    }

    public void setContacts(Map<String, Contact> contactMap)
    {
        this.contactMap = contactMap;
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

    public ContactSettings getSpecificContactSettings (String contactKey)
    {
        return contactMap.get(contactKey).getContactSettings();
    }
}
