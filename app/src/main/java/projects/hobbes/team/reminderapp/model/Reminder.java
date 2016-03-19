package projects.hobbes.team.reminderapp.model;

import java.util.Date;

/**
 * Created by Cory on 3/15/2016.
 */
public class Reminder {

    private Contact contact;
    private String app;
    private String message;
    private Date timeReceived;
    private int timeSinceReceived;
    private boolean isOverdue;

    public Reminder() {

    }

    public Reminder(Contact contact, String app, String message, Date timeReceived, int timeSinceReceived, boolean isOverdue) {
        this.contact = contact;
        this.app = app;
        this.message = message;
        this.timeReceived = timeReceived;
        this.timeSinceReceived = timeSinceReceived;
        this.isOverdue = isOverdue;
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

    public void setIsOverdue(boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
}
