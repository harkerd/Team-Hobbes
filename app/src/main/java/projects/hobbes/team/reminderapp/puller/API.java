package projects.hobbes.team.reminderapp.puller;

import android.content.Context;

import java.util.List;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;

public interface API
{
    List<Reminder> getMessages(Context context);
    List<Contact> getContacts(Context context);

    void launchActivity(Contact contact, Context context);
}
