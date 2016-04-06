package projects.hobbes.team.reminderapp.puller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;

/**
 * Created by Cory on 3/30/2016.
 */
public class FakeMessenger implements API {

    int time1 = 30;
    int time2 = 40;
    int time3 = 10;
    int time4 = 1;
    int count = 0;

    List<Reminder> reminders = new ArrayList<>();
    Reminder reminder1 = new Reminder("John Doe", "Messenger", "What are you up to?", new Date(new Date().getTime() - 30*1000*60), time1);
    Reminder reminder2 = new Reminder("John Smith", "Messenger", "Knock knock!", new Date(new Date().getTime() - 40*1000*60), time2);
    Reminder reminder3 = new Reminder("Bosco", "Messenger", "What's up dude?", new Date(new Date().getTime() - 10*1000*60), time3);
    Reminder reminder4 = new Reminder("James Bond", "Messenger", "score!", new Date(), time4);

    List<Contact> contacts = new ArrayList<>();

    public FakeMessenger() {
        reminders.add(reminder2);
        reminders.add(reminder1);
        reminders.add(reminder3);
        contacts.add(new Contact("John Doe"));
        contacts.add(new Contact("John Smith"));
        contacts.add(new Contact("Jane Doe"));
        contacts.add(new Contact("Bosco"));
        contacts.add(new Contact("James Bond"));
        contacts.add(new Contact("Zoolander"));
    }

    @Override
    public List<Reminder> getMessages(Context context) {

        count++;
        Log.d("FakeMessenger", "count: " + count);
        if (count == 12) {
//            if (reminders.size() != 4) {
//                Log.d("FakeMessenger", "adding reminder4");
//                reminders.add(reminder4);
//            }
            count = 0;
        }
        return new ArrayList<Reminder>(reminders);
    }

    @Override
    public List<Contact> getContacts(Context context) {
        return new ArrayList<>(contacts);
    }

    @Override
    public void launchActivity(Contact contact, Context context) {
        Log.d("FakeMessenger", "launching activity");
        Toast.makeText(context, "This will actually launch the Messenger app in the real thing", Toast.LENGTH_SHORT).show();
    }
}
