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
}
