package projects.hobbes.team.reminderapp.expandableReminderList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;


import java.util.List;

import projects.hobbes.team.reminderapp.AppSettingsActivity;
import projects.hobbes.team.reminderapp.R;
import projects.hobbes.team.reminderapp.model.Reminder;


public class MyExpandableAdapter extends ExpandableRecyclerAdapter<MyParentViewHolder, ReminderViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private Drawable settingsIcon;
    private Drawable maleIcon;

    public MyExpandableAdapter(Context context, List<ParentListItem> parentObjects) {
        super(parentObjects);

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        settingsIcon = new IconDrawable(context, Iconify.IconValue.fa_gear)
                .colorRes(android.R.color.background_light).sizeDp(40);
        maleIcon = new IconDrawable(context, Iconify.IconValue.fa_male)
                .colorRes(android.R.color.holo_blue_light)
                .sizeDp(40);
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.parent_list_item, viewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public ReminderViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.reminder_layout, viewGroup, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(MyParentViewHolder myParentViewHolder, int i, ParentListItem parentListItem) {
        MyParentObject parentObject = (MyParentObject) parentListItem;
        myParentViewHolder.parentTitleTextView.setText(parentObject.getTitle());
        myParentViewHolder.parentNumberIcon.setText(String.valueOf(parentObject.getChildItemList().size()));
        myParentViewHolder.parentSettingsCog.setImageDrawable(settingsIcon);
        myParentViewHolder.parentSettingsCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppSettingsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindChildViewHolder(final ReminderViewHolder reminderViewHolder, int i, Object childListItem) {
        final Reminder reminder = (Reminder) childListItem;
        reminderViewHolder.contactPicView.setImageDrawable(maleIcon);
        reminderViewHolder.contactNameTextView.setText(reminder.getContact().getName());
        String time = String.valueOf(reminder.getTimeSinceReceived()) + " minutes ago";
        reminderViewHolder.timeTextView.setText(time);
        reminderViewHolder.messageTextView.setText(reminder.getMessage());
        if (reminder.isOverdue()) {
            reminderViewHolder.timeTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        reminderViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderViewHolder.buttonsSpot.getChildCount() == 0) {
                    // add buttons
                    Button replyButton = new Button(context);
                    replyButton.setText("Reply");
                    replyButton.setGravity(1);
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //respond to message
                        }
                    });

                    Button snoozeButton = new Button(context);
                    snoozeButton.setText("Snooze");
                    replyButton.setGravity(1);
                    snoozeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do snooze
                        }
                    });

                    Button ignoreButton = new Button(context);
                    ignoreButton.setText("Ignore");
                    replyButton.setGravity(1);
                    ignoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // remove from list
                        }
                    });

                    reminderViewHolder.buttonsSpot.addView(replyButton);
                    reminderViewHolder.buttonsSpot.addView(snoozeButton);
                    reminderViewHolder.buttonsSpot.addView(ignoreButton);
                }
                else {
                    reminderViewHolder.buttonsSpot.removeAllViews();
                }
            }
        });

    }
}
