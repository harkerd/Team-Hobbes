package projects.hobbes.team.reminderapp.model;

import android.media.Image;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    private ContactSettings contactSettings;
    private String name;
    private List<String> contactInfo;
    private transient Uri image;

    public Contact(String name) {
        this.name = name;
        contactSettings = new ContactSettings();
        if (contactInfo == null) {
            this.contactInfo = new ArrayList<>();
        }
    }

    public Contact(String name, List<String> contactInfo, Uri image) {
        this.name = name;
        this.contactInfo = contactInfo;
        if (contactInfo == null) {
            this.contactInfo = new ArrayList<>();
        }
        this.image = image;
        contactSettings = new ContactSettings();
    }

    public Contact(ContactSettings contactSettings, String name, List<String> contactInfo) {
        this.contactSettings = contactSettings;
        this.name = name;
        this.contactInfo = contactInfo;
        if (contactInfo == null) {
            this.contactInfo = new ArrayList<>();
        }
    }

    public Uri getImage()
    {
        return image;
    }

    public String getName()
    {
        return name;
    }

    public ContactSettings getContactSettings() {
        if (contactSettings == null) {
            contactSettings = new ContactSettings();
        }
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
        if (contactInfo == null) {
            this.contactInfo = new ArrayList<>();
        }
    }
}
