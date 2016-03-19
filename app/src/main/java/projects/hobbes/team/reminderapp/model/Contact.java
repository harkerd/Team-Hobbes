package projects.hobbes.team.reminderapp.model;

import android.media.Image;

public class Contact {

    private ContactSettings contactSettings;
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

    public Contact(ContactSettings contactSettings, String name, String contactInfo) {
        this.contactSettings = contactSettings;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public ContactSettings getContactSettings() {
        return contactSettings;
    }

    public void setContactSettings(ContactSettings contactSettings) {
        this.contactSettings = contactSettings;
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
