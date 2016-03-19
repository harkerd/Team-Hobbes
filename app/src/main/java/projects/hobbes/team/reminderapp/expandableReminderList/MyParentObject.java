package projects.hobbes.team.reminderapp.expandableReminderList;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import projects.hobbes.team.reminderapp.model.Reminder;

/**
 * Created by cory on 11/30/15.
 */
public class MyParentObject implements ParentListItem {

    private List<Reminder> childItemList;
    private String title;

    public MyParentObject(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setChildItemList(List<Reminder> childItemList) {
        this.childItemList = childItemList;
    }

    @Override
    public List<Reminder> getChildItemList() {
        return childItemList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
