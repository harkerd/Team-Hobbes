package projects.hobbes.team.reminderapp.messenger;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
/**
 * Created by Daniel on 3/31/2016.
 */
public class Messenger extends AppCompatActivity
{
    ArrayList<Message> smsList = new ArrayList<Message>();
    ArrayList<String> contactsList = new ArrayList<String>();

    public ArrayList<String> getSmsContacts()
    {


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
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = cursor.getColumnIndex(SmsReceiver.BODY);
        int indexRead = cursor.getColumnIndex( SmsReceiver.READ );
        int indexAddr = cursor.getColumnIndex( SmsReceiver.ADDRESS );

        if ( indexBody < 0 || !cursor.moveToFirst() ) return null;

        smsList.clear();

        do
        {
            Message newMessage = new Message(cursor.getString( indexAddr ), decryptObject(cursor.getString(indexBody)), decryptObject(cursor.getString(indexRead)));
            smsList.add(newMessage);
        }
        while( cursor.moveToNext() );

        return smsList;
    }
}
