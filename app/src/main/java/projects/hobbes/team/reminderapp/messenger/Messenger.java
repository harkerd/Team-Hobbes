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
import java.util.List;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger implements API
{
    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String DATE_SENT = "date_sent";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public List<Contact> getSmsContacts(Context context)
    {
        List<Contact> contactsList = new ArrayList<Contact>();

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
                        String currPhoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        StringBuilder phoneNumber = new StringBuilder("");

                        for(int i = 0; i < currPhoneNum.length(); i++)
                        {
                            char currChar = currPhoneNum.charAt(i);

                            if(Character.isDigit(currChar))
                            {
                                phoneNumber.append(currChar);
                            }
                        }

                        phoneNumbers.add(phoneNumber.toString());
                    }
                    pCur.close();

                    Contact newContact = null;

                    if (phoneNumbers.isEmpty())
                    {
                        newContact = new Contact(name);
                    }
                    else
                    {
                        newContact = new Contact(name,phoneNumbers,null);
                    }

                    contactsList.add(newContact);
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

    //Returns empty list if no messages, otherwise returns a list of all unread messages
    @Override
    public List<Reminder> getMessages(Context context)
    {
        List<Reminder> smsList = new ArrayList<Reminder>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor inboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        Cursor outboxCursor = contentResolver.query(Uri.parse("content://sms/outbox"), null, null, null, null);

        //Inbox Column Indexes
        int indexBody = inboxCursor.getColumnIndex(BODY);
        int indexRead = inboxCursor.getColumnIndex(READ);
        int indexDate = inboxCursor.getColumnIndex(DATE);
        int indexAddr = inboxCursor.getColumnIndex(ADDRESS);

        //Outbox Column Indexes
        int indexDateSent = outboxCursor.getColumnIndex(DATE_SENT);
        int indexSentAddress = outboxCursor.getColumnIndex(ADDRESS);

        if ( indexBody < 0 || !inboxCursor.moveToFirst() ) return null;

        do
        {
            String read = inboxCursor.getString(indexRead);
            long receiveDate = inboxCursor.getLong(indexDate);
            String address = inboxCursor.getString(indexAddr);
            boolean isUnrepliedMessage = false;

            if(read.equals("0"))
            {
                isUnrepliedMessage = true;
            }
            else
            {
                do
                {
                    long sentDate = outboxCursor.getLong(indexDateSent);
                    String sentAddress = outboxCursor.getString(indexSentAddress);

                    //if current sent message was sent after receiving message on the inbox
                    if(sentDate > receiveDate)
                    {
                        //if they have the same number it means message has been already been replied to,
                        //do not include it
                        if(sentAddress.equals(address))
                        {
                            break;
                        }
                    }
                    //if the current sent message was sent before receiving message on inbox
                    //we know for sure it has not been replied to since we didn't get a match on
                    //the previous if
                    else
                    {
                        isUnrepliedMessage = true;
                        break;
                    }
                }
                while(outboxCursor.moveToNext());
            }

            //Add only messages that have not been replied to
            if(isUnrepliedMessage)
            {
                Date date = new Date(receiveDate);
                String message = inboxCursor.getString(indexBody);
                Reminder newReminder = new Reminder(address, "Messenger", message, date);

                smsList.add(newReminder);
            }
        }
        while( inboxCursor.moveToNext() );

        //todo also, if possible we only want the messages that we haven't responded to.

        return smsList;
    }

    @Override
    public List<Contact> getContacts(Context context) {
        return getSmsContacts(context);
    }

    @Override
    public void launchActivity(Contact contact, Context context)
    {
        //todo this will launch the actual Messenger app, not needed for user testing
        Log.d("Messenger", "launching activity");
        Toast.makeText(context, "This will actually launch the Messenger app in the real thing", Toast.LENGTH_SHORT).show();
    }
}
