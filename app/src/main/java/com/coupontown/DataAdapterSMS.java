package com.coupontown;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapterSMS extends RecyclerView.Adapter<DataAdapterSMS.ViewHolder> implements Filterable {
    private ArrayList<String> mArrayList;
    private ArrayList<String> mFilteredList;

    public DataAdapterSMS(ArrayList<String> arrayList) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @Override
    public DataAdapterSMS.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sms_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapterSMS.ViewHolder viewHolder, int i) {

        viewHolder.sms_text.setText(mFilteredList.get(i).toString());

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sms_text;


        public ViewHolder(View view) {
            super(view);

            sms_text = view.findViewById(R.id.sms);

        }
    }

}