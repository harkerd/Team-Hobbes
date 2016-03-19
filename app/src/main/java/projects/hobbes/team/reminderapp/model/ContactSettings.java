package projects.hobbes.team.reminderapp.model;

/**
 * Created by Cory on 3/15/2016.
 */
public class ContactSettings
{

    private String sound;
    private String reminderTime;
    private boolean isVibrateOn;

    public ContactSettings() {

    }

    public ContactSettings(String sound, String reminderTime, boolean isVibrateOn) {
        this.sound = sound;
        this.reminderTime = reminderTime;
        this.isVibrateOn = isVibrateOn;
    }
}
