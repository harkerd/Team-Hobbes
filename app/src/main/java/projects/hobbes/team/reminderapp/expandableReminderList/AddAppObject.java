package projects.hobbes.team.reminderapp.expandableReminderList;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by Cory on 3/29/2016.
 */
public class AddAppObject implements ParentListItem {
    @Override
    public List<?> getChildItemList() {
        return null;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
