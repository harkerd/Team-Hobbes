package projects.hobbes.team.reminderapp.messenger;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
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
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger implements API
{
    public static final String ADDRESS = "address";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String BODY = "body";

    public List<Contact> getSmsContacts(Context context)
    {
        List<Contact> contactsList = new ArrayList<Contact>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur == null) {
            return contactsList;
        }
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

                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            //This segment is used to remove any characters from phone numbers that are not numbers
                            //This was necessary to make them the same number that are extracted from messages
                            String currPhoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            StringBuilder phoneNumber = new StringBuilder("");

                            for (int i = 0; i < currPhoneNum.length(); i++) {
                                char currChar = currPhoneNum.charAt(i);

                                if (Character.isDigit(currChar)) {
                                    phoneNumber.append(currChar);
                                }
                            }

                            phoneNumbers.add(phoneNumber.toString());
                        }
                        pCur.close();
                    }

                    if(phoneNumbers.isEmpty())
                        continue;

                    //Extract image uri
                    Uri imageURI = getContactImage(context, id);

                    contactsList.add(new Contact(name,phoneNumbers,imageURI));
                }
            }
        }
        else {
            cur.close();
            return contactsList;
        }

        Collections.sort(contactsList, new ContactComparator());

        cur.close();
        return contactsList;
    }

    private Uri getContactImage(Context context, String id)
    {
        try (Cursor cur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                        + ContactsContract.Data.MIMETYPE + "='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                null)) {
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));

        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
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
        Set<String> alreadyGotMostRecent = new TreeSet<String>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor inboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        Cursor sentCursor = contentResolver.query(Uri.parse("content://sms/sent"), null, null, null, null);

        //SMS Column Indexes
        if (inboxCursor == null || sentCursor == null) {
            if (inboxCursor != null) {
                inboxCursor.close();
            }
            if (sentCursor != null) {
                sentCursor.close();
            }
            return null;
        }
        int indexBody = inboxCursor.getColumnIndex(BODY);
        int indexRead = inboxCursor.getColumnIndex(READ);
        int indexDate = inboxCursor.getColumnIndex(DATE);
        int indexAddr = inboxCursor.getColumnIndex(ADDRESS);

        long weeksBackMilliseconds = Long.valueOf("2419200000");
        long millisecondsBack = new Date().getTime() - weeksBackMilliseconds;

        if ( indexBody < 0 || !inboxCursor.moveToFirst() ) return smsList;

        do
        {
            long receiveDate = inboxCursor.getLong(indexDate);

            //We only want messages up to 4 weeks old
            if(receiveDate < millisecondsBack)
                break;

            String address = inboxCursor.getString(indexAddr);

            //if it is not a proper phone number we probably cannot even or
            //don't even want to reply to it (ie. message for 2 factor authentication)
            if(address.length() < 10)
                continue;

            if(alreadyGotMostRecent.contains(address))
            {
                continue;
            }

            String read = inboxCursor.getString(indexRead);
            boolean isUnrepliedMessage = false;

            if(read.equals("0"))
            {
                alreadyGotMostRecent.add(address);
                isUnrepliedMessage = true;
            }
            else
            {
                if(sentCursor.moveToFirst())
                {
                    do
                    {
                        long sentDate = sentCursor.getLong(indexDate);
                        String sentAddress = sentCursor.getString(indexAddr);

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
                        else if(sentDate < receiveDate)
                        {
                            alreadyGotMostRecent.add(address);
                            isUnrepliedMessage = true;
                            break;
                        }
                    }
                    while(sentCursor.moveToNext());
                }
                else
                {
                    isUnrepliedMessage = true;
                    break;
                }
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

        inboxCursor.close();
        sentCursor.close();
        return smsList;
    }

    @Override
    public List<Contact> getContacts(Context context) {
        return getSmsContacts(context);
    }

    @Override
    public void launchActivity(Contact contact, Context context)
    {
        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra("address", contact.getContactInfo().get(0));

        if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
        // any app that support this intent.
        {
            sendIntent.setPackage(defaultSmsPackageName);
        }
        context.startActivity(sendIntent);
    }
}
