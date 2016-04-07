package projects.hobbes.team.reminderapp.expandableReminderList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import projects.hobbes.team.reminderapp.AppSettingsActivity;
import projects.hobbes.team.reminderapp.MainActivity;
import projects.hobbes.team.reminderapp.R;
import projects.hobbes.team.reminderapp.SettingsActivity;
import projects.hobbes.team.reminderapp.model.Contact;
import projects.hobbes.team.reminderapp.model.Reminder;
import projects.hobbes.team.reminderapp.model.SettingsModel;
import projects.hobbes.team.reminderapp.puller.Puller;


public class MyExpandableAdapter extends ExpandableRecyclerAdapter<ParentViewHolder, ReminderViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private Drawable settingsIcon;
    private Drawable maleIcon;
    private Drawable addAppSettingsIcon;
    private Set<Reminder> expandedReminders = new HashSet<>();


    public MyExpandableAdapter(Context context, List<ParentListItem> parentObjects) {
        super(parentObjects);

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        settingsIcon = new IconDrawable(context, FontAwesomeIcons.fa_gear)
                .colorRes(R.color.light_grey)
                .sizeDp(40);
        maleIcon = new IconDrawable(context, FontAwesomeIcons.fa_user)
                .colorRes(android.R.color.white)
                .sizeDp(40);
        addAppSettingsIcon = new IconDrawable(context, FontAwesomeIcons.fa_gear)
                .colorRes(android.R.color.transparent)
                .sizeDp(40)
                .alpha(0);
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
    public void onBindParentViewHolder(ParentViewHolder myParentViewHolder, int i, ParentListItem parentListItem) {
        final MyParentViewHolder parentViewHolder = (MyParentViewHolder) myParentViewHolder;
        if (parentListItem instanceof MyParentObject) {
            MyParentObject parentObject = (MyParentObject) parentListItem;
            parentViewHolder.parentTitleTextView.setText(parentObject.getTitle());
            if (SettingsModel.getInstance().getAppSettings(parentObject.getTitle()).isTurnedOn()) {
                parentViewHolder.parentNumberIcon.setText(String.valueOf(parentObject.getChildItemList().size()));
            }
            else {
                parentViewHolder.parentNumberIcon.setText("X");
            }
            parentViewHolder.parentSettingsCog.setImageDrawable(settingsIcon);
            parentViewHolder.parentSettingsCog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AppSettingsActivity.class);
                    context.startActivity(intent);
                }
            });
            parentViewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentViewHolder.onClick(v);
                }
            });
        }
        else if (parentListItem instanceof AddAppObject) {
            parentViewHolder.parentSettingsCog.setImageDrawable(addAppSettingsIcon);
            parentViewHolder.parentTitleTextView.setText("Add App");
            parentViewHolder.parentNumberIcon.setText("+");
            parentViewHolder.parentNumberIcon.setTextSize(30);
            parentViewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ExpandableAdapter", "add app");
                    DialogFragment newFragment = new AddAppDialogFragment();
                    newFragment.show(((MainActivity)context).getSupportFragmentManager(), "add app");
                }
            });
        }
    }

    @Override
    public void onBindChildViewHolder(final ReminderViewHolder reminderViewHolder, final int i, Object childListItem) {
        final Reminder reminder = (Reminder) childListItem;
        reminderViewHolder.contactPicView.setImageDrawable(maleIcon);
        reminderViewHolder.contactPicView.setBackgroundResource(R.color.blue);
        reminderViewHolder.contactPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("contactPic", "clicked on " + reminder.getContact().getName() + "'s contact pic");
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra("appName", reminder.getApp());
                intent.putExtra("contactName", reminder.getContactName());
                context.startActivity(intent);
            }
        });
        reminderViewHolder.contactNameTextView.setText(reminder.getContactName());
        int timeSince = reminder.getTimeSinceReceived(); // may use this to change the text if more than an hour or day
        String time;
        time = String.valueOf(reminder.getTimeSinceReceived()) + " minutes ago";
        reminderViewHolder.timeTextView.setText(time);
        reminderViewHolder.messageTextView.setText(reminder.getMessage());
        if (reminder.isOverdue()) {
            reminderViewHolder.timeTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        else {
            reminderViewHolder.timeTextView.setTextColor(context.getResources().getColor(android.R.color.tertiary_text_light));
        }

        reminderViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderViewHolder.buttonsSpot.getChildCount() == 0) {
                    expandedReminders.add(reminder);
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
                            SettingsModel.getInstance().getAppSettings(reminder.getApp()).getAPI()
                                    .launchActivity(reminder.getContact(), context);
                        }
                    });

                    Button snoozeButton = new Button(context);
                    snoozeButton.setText("Snooze");
                    snoozeButton.setLayoutParams(params);
                    snoozeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do snooze
                            String reminderTime = reminder.getContact().getContactSettings().getReminderTime();
                            Date remindTime = new Date(new Date().getTime() + Puller.stringToMilSeconds(reminderTime));
                            reminder.updateData(reminder.getContact(), remindTime);
                            MyExpandableAdapter.this.notifyItemChanged(i);
                        }
                    });

                    Button ignoreButton = new Button(context);
                    ignoreButton.setText("Ignore");
                    ignoreButton.setLayoutParams(params);
                    ignoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // remove from list
                            //todo implement this
                            Log.d("ExpandableAdapter", "ignore button pushed");
                            Toast.makeText(context, "This will actually remove the reminder in the real thing", Toast.LENGTH_SHORT).show();
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
                    expandedReminders.remove(reminder);
                }
            }
        });

        if (expandedReminders.contains(reminder)) {
            reminderViewHolder.buttonsSpot.removeAllViews();
            reminderViewHolder.item.callOnClick();
        }
        else {
            reminderViewHolder.buttonsSpot.removeAllViews();
        }

    }

    public static class AddAppDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //todo: in the real thing, this needs to be implemented
            builder.setMessage("There are no other apps supported to add!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // This will add the app
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
