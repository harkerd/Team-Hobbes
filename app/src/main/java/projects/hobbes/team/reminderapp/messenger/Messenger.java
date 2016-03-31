package projects.hobbes.team.reminderapp.messenger;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import projects.hobbes.team.reminderapp.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger extends AppCompatActivity
{
    private Map<String,String> getIDToNames()
    {
        Map<String,String> idToName = new HashMap<String,String>();

        ContentResolver cr = getContentResolver();
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

    public ArrayList<Contact> getSmsContacts()
    {
        ArrayList<Contact> contactsList = new ArrayList<Contact>();

        ContentResolver cr = getContentResolver();
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
    public ArrayList<Message> getSmsMessages()
    {
        ArrayList<Message> smsList = new ArrayList<Message>();
        Map<String,String> idToName = getIDToNames();

        ContentResolver contentResolver = getContentResolver();
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
}
