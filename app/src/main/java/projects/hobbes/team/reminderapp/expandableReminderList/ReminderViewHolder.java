package projects.hobbes.team.reminderapp.expandableReminderList;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import projects.hobbes.team.reminderapp.R;


public class ReminderViewHolder extends ChildViewHolder {

    public LinearLayout item;
    public ImageView contactPicView;
    public TextView contactNameTextView;
    public TextView timeTextView;
    public TextView messageTextView;
    public LinearLayout buttonsSpot;

    public ReminderViewHolder(View itemView) {
        super(itemView);

        item = (LinearLayout) itemView.findViewById(R.id.item);
        contactPicView = (ImageView) itemView.findViewById(R.id.contact_pic_reminder);
        contactNameTextView = (TextView) itemView.findViewById(R.id.contact_name_reminder);
        timeTextView = (TextView) itemView.findViewById(R.id.time_reminder);
        messageTextView = (TextView) itemView.findViewById(R.id.message_content_reminder);
        buttonsSpot = (LinearLayout) itemView.findViewById(R.id.buttons_spot);
    }
}