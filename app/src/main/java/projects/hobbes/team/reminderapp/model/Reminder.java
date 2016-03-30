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

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    public int getTimeSinceReceived() {
        return timeSinceReceived;
    }

    public void setTimeSinceReceived(int timeSinceReceived) {
        this.timeSinceReceived = timeSinceReceived;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public boolean isIgnored() {
        return ignore;
    }

    public void setIsOverdue(boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public boolean equals(Reminder r) {
         return r.getApp() == this.getApp() &&
            r.getMessage() == this.getMessage() &&
            r.getContact().getName() == this.getContact().getName() &&
            r.timeReceived.equals(this.timeReceived);
    }
}
