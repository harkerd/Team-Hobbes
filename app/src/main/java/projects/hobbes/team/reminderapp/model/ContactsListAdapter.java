package projects.hobbes.team.reminderapp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.hobbes.team.reminderapp.R;
import projects.hobbes.team.reminderapp.SettingsActivity;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactHolder>
{
    private Context context;
    private List<Contact> contacts;

    public ContactsListAdapter(Context context, Map<String, Contact> contactMap)
    {
        this.context = context;
        this.contacts = new ArrayList<>();
        for(String contactName : contactMap.keySet())
        {
            contacts.add(contactMap.get(contactName));
        }
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
                    context.startActivity(intent);
                }
            });
        }

        public void bindItem(Contact contact)
        {
            this.contact = contact;

            ImageView iconView = (ImageView) itemView.findViewById(R.id.contact_icon);
            TextView textView = (TextView) itemView.findViewById(R.id.contact_name);

            if(contact.getImage() == null)
            {
                Drawable icon = new IconDrawable(context, FontAwesomeIcons.fa_user).color(Color.WHITE).sizeDp(60);
                iconView.setImageDrawable(icon);
            }
            else
            {
                //TODO: figure out actual picture...
            }

            textView.setText(contact.getName());
        }
    }
}
