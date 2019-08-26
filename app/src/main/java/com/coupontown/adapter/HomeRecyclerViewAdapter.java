package com.coupontown.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.coupontown.MoreDetailsActivity;
import com.coupontown.R;
import com.coupontown.model.Favourite;
import com.coupontown.model.ItemOfferModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ItemViewHolder> implements Filterable {

    private List<ItemOfferModel> itemList;

    private List<ItemOfferModel> mFilteredList;

    private Context context;

    private List<ItemOfferModel> favList;

    private static final String TAG_HOMEACTIVITY = HomeRecyclerViewAdapter.class.getName();

    private DatabaseReference mFirebaseDatabase;


    public HomeRecyclerViewAdapter(Context context, List<ItemOfferModel> itemList) {
        this.itemList = itemList;
        this.mFilteredList = itemList;
        this.context = context;
    }

    public HomeRecyclerViewAdapter(Context context, List<ItemOfferModel> itemList, Favourite favList) {
        this.favList = favList.getItemOfferModels();
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
    public void onBindViewHolder(@NonNull final ItemViewHolder viewHolder, final int position) {
        // viewHolder.tvItem.setText(itemList.get(position));
        viewHolder.tv_name.setText(mFilteredList.get(position).getName());
        viewHolder.tv_cat.setText(mFilteredList.get(position).getCategory());
        viewHolder.tv_item_desc.setText(mFilteredList.get(position).getDescription());

        viewHolder.status.setText(mFilteredList.get(position).getStatus());

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("favorites").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ItemOfferModel itemOfferModel = mFilteredList.get(position);
            if (itemOfferModel.isFav()) {
            // itemOfferModel.setFav(Boolean.TRUE);
            viewHolder.likeButton.setLiked(true);
        }

        viewHolder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (favList == null) {
                    favList = new ArrayList<>();
                }

                ItemOfferModel itemOfferModel = mFilteredList.get(position);

                itemOfferModel.setFav(true);
                favList.add(itemOfferModel);
                mFirebaseDatabase.setValue(favList);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ItemOfferModel itemOfferModel = mFilteredList.get(position);
                Log.i("UnLiked ::==>", likeButton.toString());
                itemOfferModel.setFav(false);

                if (favList != null && favList.size() > 0) {
                    favList.remove(itemOfferModel);
                }
                //TODO need to delete respective index for performance and optimization
                // databaseReference.child("favorites").removeValue();
                System.out.println("My FAv List in unLiked " + favList);
                mFirebaseDatabase.setValue(favList);
            }
        });


        viewHolder.sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, viewHolder.tv_name.getText() + "\n" + viewHolder.tv_item_desc + "\n" + viewHolder.tv_cat);
                sendIntent.setType("text/plain");
                Log.i(TAG_HOMEACTIVITY, "Here in Button Click");
                v.getContext().startActivity(sendIntent);
            }
        });
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


        private TextView tv_name, tv_cat, tv_item_desc, status;
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
            status = view.findViewById(R.id.status);

            AlphaAnimation alphaAnimation = blinkText();
            status.startAnimation(alphaAnimation);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Log.d(TAG_HOMEACTIVITY, "Selected" + position + "View ID:" + view.getId());
            Log.d(TAG_HOMEACTIVITY, itemList.get(position).getName());
            redirecttoMoreDetails(position);

        }
    }

    private AlphaAnimation blinkText() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }

    private void redirecttoMoreDetails(int position) {
        Intent intent = new Intent(context, MoreDetailsActivity.class);
        intent.putExtra("itemdetails", itemList.get(position));
        context.startActivity(intent);
    }
}
