package projects.hobbes.team.reminderapp.expandableReminderList;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import projects.hobbes.team.reminderapp.R;


/**
 * Created by cory on 11/30/15.
 */
public class MyParentViewHolder extends ParentViewHolder {

    public TextView parentTitleTextView;
    public ImageView parentSettingsCog;
    public TextView parentNumberIcon;
    public LinearLayout itemContainer;

    public MyParentViewHolder(View itemView) {
        super(itemView);
        parentTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_text);
        parentNumberIcon = (TextView) itemView.findViewById(R.id.parent_list_item_number);
        parentSettingsCog = (ImageView) itemView.findViewById(R.id.parent_list_item_cog);
        itemContainer = (LinearLayout) itemView.findViewById(R.id.parent_list_item);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
    }
}
