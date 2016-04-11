package projects.hobbes.team.reminderapp.model;

import java.util.Map;
import java.util.TreeMap;

import projects.hobbes.team.reminderapp.messenger.Messenger;
import projects.hobbes.team.reminderapp.puller.API;

public class AppSettings {

    private String appName;
    private boolean isTurnedOn = true;
    private ContactSettings defaultContactSettings;
    private Map<String, Contact> contactMap;
    private transient API api;

    public AppSettings(String appName) {
        defaultContactSettings = new ContactSettings();
        contactMap = new TreeMap<>();
        this.appName = appName;
    }

    public ContactSettings getDefaultContactSettings() {
        return new ContactSettings();
    }

    public void setContacts(Map<String, Contact> contactMap)
    {
        this.contactMap = contactMap;
    }

    public Map<String, Contact> getContactMap() {
        return contactMap;
    }

    public Contact getContact(String name) {
        return contactMap.get(name);
    }

    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    public void toggleIsTurnedOn() {
        isTurnedOn = !isTurnedOn;
    }

    public API getAPI() {
        if(api == null)
        {
            api = createAPI(appName);
        }
        return api;
    }

    public ContactSettings getSpecificContactSettings (String contactKey)
    {
        if (contactMap.get(contactKey) == null) {
            return getDefaultContactSettings();
        }
        return contactMap.get(contactKey).getContactSettings();
    }

    public static API createAPI(String appName)
    {
        if(appName.equals("Messenger"))
        {
            return new Messenger();
        }
        else
        {
            System.err.println("UnsupportedAPIException");
            return null;
        }
    }
}
