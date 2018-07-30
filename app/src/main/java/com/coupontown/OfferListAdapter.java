package com.coupontown;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class OfferListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;

    public OfferListAdapter(Context context, List<String> expandableListTitle, Map<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }


    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return expandableListDetail.get(this.expandableListTitle.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.expandableListTitle.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.expandableListDetail.get(this.expandableListTitle.get(i)).get(i1);

    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String listTitles = (String) getGroup(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.listgroup_offers, null);
        }

        TextView textView = view.findViewById(R.id.listTitle);

        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(listTitles);


        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean b, View view, ViewGroup viewGroup) {

        final String expandableList = (String) getChild(listPosition, expandedListPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_offers, null);
        }

        TextView expandedView = view.findViewById(R.id.expandedListItem);
        expandedView.setText(expandableList);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void filterData(String query){
        query = query.toLowerCase();
        Log.i("OfferListAdaptyer" , String.valueOf(expandableListTitle.size()));

       for(String msg: expandableListTitle){
           if(msg.contains(query)){
               Log.i("Inside Msg" , msg);
               notifyDataSetChanged();
           }
       }

    }
}
