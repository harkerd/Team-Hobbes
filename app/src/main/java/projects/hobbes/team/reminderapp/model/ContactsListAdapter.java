package projects.hobbes.team.reminderapp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import projects.hobbes.team.reminderapp.R;
import projects.hobbes.team.reminderapp.SettingsActivity;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactHolder>
{
    private Context context;
    private List<Contact> contacts;
    private String appName;

    public ContactsListAdapter(Context context, Map<String, Contact> contactMap, String appName)
    {
        this.context = context;
        setData(contactMap, appName);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.contact_layout, null);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position)
    {
        holder.bindItem(contacts.get(position));
    }

    @Override
    public int getItemCount()
    {
        return contacts.size();
    }

    public void setData(Map<String, Contact> contactMap, String appName)
    {
        this.contacts = new ArrayList<>();
        this.appName = appName;
        for(String contactName : contactMap.keySet())
        {
            contacts.add(contactMap.get(contactName));
        }
        notifyDataSetChanged();
    }

    public class ContactHolder extends RecyclerView.ViewHolder
    {
        private Contact contact;

        public ContactHolder(final View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    intent.putExtra("contactName", contact.getName());
                    intent.putExtra("appName", appName);
                    context.startActivity(intent);
                }
            });
        }

        public void bindItem(Contact contact)
        {
            this.contact = contact;

            ImageView iconView = (ImageView) itemView.findViewById(R.id.contact_icon);
            TextView textView = (TextView) itemView.findViewById(R.id.contact_name);

            Drawable icon = new IconDrawable(context, FontAwesomeIcons.fa_user).color(Color.WHITE).sizeDp(60);
            try
            {
                Uri uri = contact.getImage();
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                icon = Drawable.createFromStream(inputStream, uri.toString());

                Bitmap b = ((BitmapDrawable)icon).getBitmap();
                b = Bitmap.createScaledBitmap(b, 240, 240, false);
                icon = new BitmapDrawable(context.getResources(), b);
            }
            catch (FileNotFoundException e) {
                System.err.println("FileNotFound");
            }
            catch (NullPointerException e) {
                System.out.println("No image given");
            }

            iconView.setImageDrawable(icon);
            textView.setText(contact.getName());
        }
    }
}
