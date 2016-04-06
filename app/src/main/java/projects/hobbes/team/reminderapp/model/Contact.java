package projects.hobbes.team.reminderapp.model;

import android.media.Image;

import java.util.List;

public class Contact {

    private ContactSettings contactSettings;
    private String name;
    private List<String> contactInfo;
    private Image image;

    public Contact(String name) {
        this.name = name;
        contactSettings = new ContactSettings();
    }

    public Contact(String name, List<String> contactInfo, Image image) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.image = image;
        contactSettings = new ContactSettings();
    }

    public Contact(ContactSettings contactSettings, String name, List<String> contactInfo) {
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

    public List<String> getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(List<String> contactInfo) {
        this.contactInfo = contactInfo;
    }
}
