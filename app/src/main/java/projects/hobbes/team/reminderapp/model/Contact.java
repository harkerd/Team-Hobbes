package projects.hobbes.team.reminderapp.model;

import android.media.Image;

public class Contact {

    private ContactSettings contactSettings;
    private String name;
    private String contactInfo;
    private Image image;

    public Contact(String name) {
        this.name = name;
        contactSettings = new ContactSettings();
    }

    public Contact(String name, String contactInfo, Image image) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.image = image;
        contactSettings = new ContactSettings();
    }

    public Contact(ContactSettings contactSettings, String name, String contactInfo) {
        this.contactSettings = contactSettings;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public Image getImage()
    {
        return image;
    }

    public String getName()
    {
        return name;
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
