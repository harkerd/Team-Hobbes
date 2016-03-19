package projects.hobbes.team.reminderapp.model;

import android.media.Image;

public class Contact {

    private Settings settings;
    private String name;
    private String contactInfo;
    private Image image;

    public Contact(String name) {
        this.name = name;
    }

    public Image getImage()
    {
        return image;
    }

    public String getName()
    {
        return name;
    }

    public Contact(Settings settings, String name, String contactInfo) {
        this.settings = settings;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
