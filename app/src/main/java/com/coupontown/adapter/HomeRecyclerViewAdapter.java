package com.coupontown.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.coupontown.MoreDetailsActivity;
import com.coupontown.R;
import com.coupontown.model.ItemOfferModel;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ItemViewHolder> implements Filterable {

    private List<ItemOfferModel> itemList;

    private List<ItemOfferModel> mFilteredList;

    private Context context;


    public HomeRecyclerViewAdapter(Context context, List<ItemOfferModel> itemList) {
        this.itemList = itemList;
        this.mFilteredList = itemList;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        // viewHolder.tvItem.setText(itemList.get(position));
        viewHolder.tv_name.setText(mFilteredList.get(position).getName());
        viewHolder.tv_cat.setText(mFilteredList.get(position).getCategory());
        viewHolder.tv_item_desc.setText(mFilteredList.get(position).getDescription());
        viewHolder.tv_item_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

            Picasso.with(context).load(itemList.get(position)
                    .getLogo())
                    .resize(75,75)
                    .into(viewHolder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return mFilteredList == null ? 0 : mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {

                    mFilteredList = itemList;
                } else {

                    ArrayList<ItemOfferModel> filteredList = new ArrayList<>();


                    for (ItemOfferModel itemOfferModel : mFilteredList) {
                        if (itemOfferModel.getName().toLowerCase().contains(charString) ||
                                itemOfferModel.getCategory().toLowerCase().contains(charString)) {
                            filteredList.add(itemOfferModel);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                mFilteredList = (ArrayList<ItemOfferModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView tv_name, tv_cat, tv_item_desc;
        private ImageView circleImageView;
        private ImageView sharebutton;
        private LikeButton likeButton;


        public ItemViewHolder(@NonNull View view) {
            super(view);

            // tvItem = itemView.findViewById(R.id.tvItem);
            circleImageView = view.findViewById(R.id.item_id);
            tv_name = view.findViewById(R.id.tv_name);
            tv_cat = view.findViewById(R.id.tv_cat);
            tv_item_desc = view.findViewById(R.id.tv_item_desc);
            sharebutton = view.findViewById(R.id.share);
            likeButton = view.findViewById(R.id.star_button);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.i("Testing on click", "Selected" + position + "View ID:" + view.getId());
            Log.i("Item Postioned", itemList.get(position).getName());

            Toast.makeText(context, "Clicked on Item" + itemList.get(position), Toast.LENGTH_LONG);
            Snackbar.make(view, "Clicked on Item" + itemList.get(position), Snackbar.LENGTH_LONG);
            Intent intent = new Intent(context, MoreDetailsActivity.class);
            intent.putExtra("itemdetails", itemList.get(position));
            context.startActivity(intent);
        }
    }
}
