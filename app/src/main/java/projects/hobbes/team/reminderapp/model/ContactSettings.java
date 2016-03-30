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
        sound = "None";
        reminderTime = "15 Min";
        isVibrateOn = true;

    }

    public ContactSettings(String sound, String reminderTime, boolean isVibrateOn) {
        this.sound = sound;
        this.reminderTime = reminderTime;
        this.isVibrateOn = isVibrateOn;
    }

    public boolean getIsVibrateOn()
    {
        return this.isVibrateOn;
    }

    public void toggleVibrate()
    {
        this.isVibrateOn = !this.isVibrateOn;

    }


    public String getSound()
    {
        return sound;
    }

    public void setSound(String sound)
    {
        this.sound = sound;
    }

    public String getReminderTime()
    {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime)
    {
        this.reminderTime = reminderTime;
    }
}
