package com.example.vitaliy.myapplication.ViewManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.example.vitaliy.myapplication.Entity.CheckBoxItem;
import com.example.vitaliy.myapplication.R;

import java.util.ArrayList;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<CheckBoxItem>> childs;
    private LayoutInflater inflater;

    public ExpandableListViewAdapter(Context context,
                        ArrayList<String> groups,
                        ArrayList<ArrayList<CheckBoxItem>> childs ) {
        this.context = context;
        this.groups = groups;
        this.childs = childs;
        inflater = LayoutInflater.from( context );
    }

    public Object getChild(int groupPosition, int childPosition) {
        return childs.get( groupPosition ).get( childPosition );
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.child_row, parent, false);
        CheckBoxItem child = (CheckBoxItem)getChild( groupPosition, childPosition );
        TextView color = (TextView)v.findViewById( R.id.childname );
        if( color != null )
            color.setText( child.text );
        CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );
        cb.setChecked( child.state );
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return childs.get( groupPosition ).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get( groupPosition );
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false);
        String gt = (String)getGroup( groupPosition );
        TextView colorGroup = (TextView)v.findViewById( R.id.childname );
        if( gt != null )
            colorGroup.setText( gt );
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void onGroupCollapsed (int groupPosition) {}
    public void onGroupExpanded(int groupPosition) {}
}