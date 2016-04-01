package projects.hobbes.team.reminderapp.messenger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.puller.API;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger implements API
{
    Map<String,Contact> idToContact = new HashMap<String,Contact>();

    public List<Contact> getSmsContacts(Context context)
    {
        List<Contact> contactsList = new ArrayList<Contact>();
        idToContact.clear();

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
                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    ArrayList<String> phoneNumbers = new ArrayList<>();

                    while (pCur.moveToNext())
                    {
                        phoneNumbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }
                    pCur.close();

                    Contact newContact = null;

                    if (phoneNumbers.isEmpty()) {
                        newContact = new Contact(name);
                    }
                    else
                    {
                        newContact = new Contact(name,phoneNumbers.get(0),null);
                    }

                    contactsList.add(newContact);
                    idToContact.put(id,newContact);
                }
            }
        }
        else
            return contactsList;

        Collections.sort(contactsList, new ContactComparator());

        return contactsList;
    }

    public class ContactComparator implements Comparator<Contact>
    {
        @Override
        public int compare(Contact c1, Contact c2)
        {
            return c1.getName().compareTo(c2.getName());
        }
    }

    public String decryptObject(String encryptedMessage)
    {
        try
        {
            return SmsSecurityHandler.decrypt( new String(SmsReceiver.PASSWORD), encryptedMessage );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    //Returns empty list if no messages, otherwise returns a list of all unread messages
    @Override
    public List<Reminder> getMessages(Context context)
    {
        List<Reminder> smsList = new ArrayList<Reminder>();

        //Refresh our map of ids to contacts
        getSmsContacts(context);

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = cursor.getColumnIndex(SmsReceiver.BODY);
        int indexRead = cursor.getColumnIndex(SmsReceiver.READ);
        int indexDate = cursor.getColumnIndex(SmsReceiver.DATE);
        int indexID = cursor.getColumnIndex(SmsReceiver.PERSON);
        int indexAddr = cursor.getColumnIndex(SmsReceiver.ADDRESS);

        if ( indexBody < 0 || !cursor.moveToFirst() ) return null;

        do
        {
            String read = cursor.getString(indexRead);

            //Add only unread messages
            if(read.equals("0"))
            {
                Contact contact = idToContact.get(cursor.getString(indexID));

                //If there is not contact, add the contact using phone number
                if(contact == null)
                    contact = new Contact(cursor.getString(indexAddr));

                Date date = new Date();
                date.setTime(Long.parseLong(cursor.getString(indexDate)));
                String message = decryptObject(cursor.getString(indexBody));

                Reminder newReminder = new Reminder();
                newReminder.setApp("Messenger");
                newReminder.setContact(contact);
                newReminder.setIsOverdue(false);
                newReminder.setMessage(message);
                newReminder.setTimeReceived(date);
                newReminder.setTimeSinceReceived(newReminder.getTimeSinceReceived());

                smsList.add(newReminder);
            }
        }
        while( cursor.moveToNext() );

        //todo- the message bodies are all null when I run this at 9:24 pm thursday = probably because of the encryption thing...
        //todo- we only want the most recent message from each person. this list has all the messages. This means if the same person
        //todo- sent more than one message, that person is in there more than once
        //todo also, if possible we only want the messages that we haven't responded to. => don't think this is possible, best I can do right now is to get unread messages
        //todo- currently the messages have the sender being the sender's phone number,
        //todo- we want the sender's name if they are a contact in our phone (that should be a task done in the Puller
        //todo- since it will have the contact list available)

        return smsList;
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
