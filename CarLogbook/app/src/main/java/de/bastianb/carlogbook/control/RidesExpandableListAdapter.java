package de.bastianb.carlogbook.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.model.Ride;

/**
 * This class is used to control the data and the view for a ExpandableListView as a custom adapter
 */
public class RidesExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<Ride>> groupedRides;
    private List<String> groups;

    /**
     * Create a new Adapter
     *
     * @param context the Activitiy that contains the ExpandableListView
     * @param groupedRides a map with all the dates as group
     */
    public RidesExpandableListAdapter(Activity context, Map<String, List<Ride>> groupedRides) {
        this.context = context;
        this.groups = new ArrayList<>();

        updateMapAndGroups(groupedRides);
    }

    /**
     * This method is used to update the data
     *
     * @param group a map with all the dates as group
     */
    public void updateMapAndGroups(Map<String, List<Ride>> group){
        groups.clear();

        if (group.size() > 0){
            groups.addAll(group.keySet());
        }

        groupedRides = group;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        if (groupPosition < 0 || childPosititon < 0){
            return null;
        }
        return this.groupedRides.get(this.groups.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Ride selection = (Ride) getChild(groupPosition, childPosition);
        final String childText = selection.getEndTime() + " - " + selection.getGoal();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupedRides.get(groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
