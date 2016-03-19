package projects.hobbes.team.reminderapp.expandableReminderList;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import projects.hobbes.team.reminderapp.R;


/**
 * Created by cory on 11/30/15.
 */
public class MyParentViewHolder extends ParentViewHolder {

//    private static final float INITIAL_POSITION = 0.0f;
//    private static final float ROTATED_POSITION = 180f;
//    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public TextView parentTitleTextView;
    public ImageView parentSettingsCog;
    public TextView parentNumberIcon;

    public MyParentViewHolder(View itemView) {
        super(itemView);
        parentTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_text);
        parentNumberIcon = (TextView) itemView.findViewById(R.id.parent_list_item_number);
        parentSettingsCog = (ImageView) itemView.findViewById(R.id.parent_list_item_cog);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
//        if (expanded) {
////            parentDropDownArrow.setImageResource(android.R.drawable.arrow_up_float);
//            parentDropDownArrow.setImageDrawable(arrowUp);
//        }
//        else {
////            parentDropDownArrow.setImageResource(android.R.drawable.arrow_down_float);
//            parentDropDownArrow.setImageDrawable(arrowDown);
//        }
////        if (!HONEYCOMB_AND_ABOVE) {
////            return;
////        }
////
////        if (expanded) {
////            parentDropDownArrow.setRotation(ROTATED_POSITION);
////        }
////        else {
////            parentDropDownArrow.setRotation(INITIAL_POSITION);
////        }
    }
}
