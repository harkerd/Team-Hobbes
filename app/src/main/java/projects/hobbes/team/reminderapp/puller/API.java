package projects.hobbes.team.reminderapp.puller;

import java.util.List;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;

public interface API
{
    List<Reminder> getMessages();
    List<Contact> getContacts();

    void launchActivity(Contact contact);
}