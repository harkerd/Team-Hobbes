package projects.hobbes.team.reminderapp.messenger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.puller.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger implements API
{
    private Map<String,String> getIDToNames(Context context)
    {
        Map<String,String> idToName = new HashMap<String,String>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                idToName.put(id,name);
            }
        }
        else
            return null;

        return idToName;
    }

    public ArrayList<Contact> getSmsContacts(Context context)
    {
        ArrayList<Contact> contactsList = new ArrayList<Contact>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    //todo Given that this is for messenger, this should probably get the phone numbers instead of the emails.
                    //todo. also it only enters this if clause if they have a phone number. This says nothing about whether they have an email address or not
                    // get email
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                               null,
                                               ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                               new String[]{id}, null);

                    ArrayList<String> emails = new ArrayList<String>();

                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        emails.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                    emailCur.close();

                    Contact newContact =  null;
                    if(emails.isEmpty())
                    {
                        newContact = new Contact(name);
                    }
                    else
                    {
                        newContact = new Contact(name,emails.get(0),null);
                    }

                    contactsList.add(newContact);
                }
            }
        }
        else
            return null;
        //todo instead of returning null, make this return an empty list

        //todo sort this by contact name before returning
        return contactsList;
    }

    public String decryptObject(String encryptedMessage)
    {
        try
        {
            String data = SmsSecurityHandler.decrypt( new String(SmsReceiver.PASSWORD), encryptedMessage );

            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    //Returns null if it body is empty, otherwise return a complete list of all messages.
    //Use the attributes
    public ArrayList<Message> getSmsMessages(Context context)
    {
        ArrayList<Message> smsList = new ArrayList<Message>();
        Map<String,String> idToName = getIDToNames(context);

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = cursor.getColumnIndex(SmsReceiver.BODY);
        int indexRead = cursor.getColumnIndex(SmsReceiver.READ);
        int indexID = cursor.getColumnIndex(SmsReceiver.PERSON);
        int indexAddr = cursor.getColumnIndex(SmsReceiver.ADDRESS);

        if ( indexBody < 0 || !cursor.moveToFirst() ) return null;

        smsList.clear();

        do
        {
            String name = idToName.get(cursor.getString( indexID ));

            if(name == null)
                name = cursor.getString( indexAddr );

            Message newMessage = new Message(name, decryptObject(cursor.getString( indexBody )), cursor.getString(indexRead));
            smsList.add(newMessage);
        }
        while( cursor.moveToNext() );

        return smsList;
    }

    @Override
    public List<Reminder> getMessages(Context context) {
        List<Message> messages = getSmsMessages(context);
        //todo- the message bodies are all null when I run this at 9:24 pm thursday

        //todo- we only want the most recent message from each person. this list has all the messages. This means if the same person
        //todo- sent more than one message, that person is in there more than once

        //todo also, if possible we only want the messages that we haven't responded to.

        //todo- currently the messages have the sender being the sender's phone number,
        //todo- we want the sender's name if they are a contact in our phone (that should be a task done in the Puller
        //todo- since it will have the contact list available)

        //todo convert this to a list of Reminder objects and return that list
        //todo make sure that this doesn't return null. If there aren't any, return an empty list
        List<Reminder> reminders = new ArrayList<>();
        return reminders;
    }

    @Override
    public List<Contact> getContacts(Context context) {
        return getSmsContacts(context);
    }

    @Override
    public void launchActivity(Contact contact, Context context) {
        //todo this will launch the actual Messenger app, not needed for user testing
        Log.d("Messenger", "launching activity");
        Toast.makeText(context, "This will actually launch the Messenger app in the real thing", Toast.LENGTH_SHORT).show();
    }
}
