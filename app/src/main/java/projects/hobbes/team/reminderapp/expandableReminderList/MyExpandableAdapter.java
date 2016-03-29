package projects.hobbes.team.reminderapp.expandableReminderList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;


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

        settingsIcon = new IconDrawable(context, FontAwesomeIcons.fa_gear)
                .colorRes(R.color.light_grey)
                .sizeDp(40);
        maleIcon = new IconDrawable(context, FontAwesomeIcons.fa_male)
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
        reminderViewHolder.contactPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("contactPic", "clicked on " + reminder.getContact().getName() + "'s contact pic");
                //TODO: call contact settings activity for this person
            }
        });
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
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5, 5, 5, 5);
                    params.weight = 1;
                    replyButton.setLayoutParams(params);
                    //replyButton.setGravity(1);
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //respond to message
                        }
                    });

                    Button snoozeButton = new Button(context);
                    snoozeButton.setText("Snooze");
                    replyButton.setText("Reply");
                    snoozeButton.setLayoutParams(params);
                    snoozeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do snooze
                        }
                    });

                    Button ignoreButton = new Button(context);
                    ignoreButton.setText("Ignore");
                    ignoreButton.setLayoutParams(params);
                    ignoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // remove from list
                        }
                    });

                    int width = context.getResources().getDisplayMetrics().widthPixels;
                    int margin = width / 10;

                    View marginViewLeft = new View(context);
                    LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(width, 1);
                    marginParams.width = margin;
                    marginViewLeft.setLayoutParams(marginParams);
                    View marginViewRight = new View(context);
                    marginViewRight.setLayoutParams(marginParams);

                    reminderViewHolder.buttonsSpot.addView(marginViewLeft);
                    reminderViewHolder.buttonsSpot.addView(replyButton);
                    reminderViewHolder.buttonsSpot.addView(snoozeButton);
                    reminderViewHolder.buttonsSpot.addView(ignoreButton);
                    reminderViewHolder.buttonsSpot.addView(marginViewRight);
                }
                else {
                    reminderViewHolder.buttonsSpot.removeAllViews();
                }
            }
        });

    }
}
