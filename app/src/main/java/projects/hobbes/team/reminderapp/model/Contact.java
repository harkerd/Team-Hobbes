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
}
