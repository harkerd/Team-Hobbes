package projects.hobbes.team.reminderapp.model;

import java.util.Date;

/**
 * Created by Cory on 3/15/2016.
 */
public class Reminder {

    //From API
    private String contactName;
    private String app;
    private String message;
    private Date timeReceived;
    private int timeSinceReceived;

    //From Puller
    private Contact contact;
    private boolean isOverdue;
    private Date remindTime;
    private boolean ignore;

    public Reminder() {

    }

    public Reminder(String contactName, String app, String message, Date timeReceived, int timeSinceReceived) {
        this.contactName = contactName;
        this.app = app;
        this.message = message;
        this.timeReceived = timeReceived;
        this.timeSinceReceived = timeSinceReceived;
    }

    public void updateData(Contact contact, Date remindTime) {
        this.contact = contact;
        if(remindTime != null)
        {
            this.remindTime = remindTime;
        }
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContactName() {
        return contactName;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    public int getTimeSinceReceived() {
        long time = new Date().getTime() - timeReceived.getTime();
        long seconds = time / 1000;
        long minutes = seconds / 60;
        return (int)minutes;
    }

    public void setTimeSinceReceived(int timeSinceReceived) {
        this.timeSinceReceived = timeSinceReceived;
    }

    public boolean isOverdue() {
        if (new Date().after(remindTime)) {
            return true;
        }
        return isOverdue;
    }

    public boolean isIgnored() {
        return ignore;
    }

    public void setIsOverdue(boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public boolean equals(Object o) {
        if(o == null || !(o instanceof Reminder))
        {
            return false;
        }
        else
        {
            Reminder r = (Reminder) o;
            return r.app == this.app &&
                    r.message == this.message &&
                    r.contactName == this.contactName &&
                    r.timeReceived.equals(this.timeReceived);
        }
    }
}
