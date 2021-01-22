package com.monresto.acidlabs.monresto.UI.RestaurantDetails.Reviews;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Review;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Review> reviews;

    public ReviewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_review, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder viewHolder, int i) {
        Review review = reviews.get(i);
        viewHolder.name.setText(review.getClientname());
        viewHolder.ratingBar.setRating((float) review.getNote());
        viewHolder.ratingBar.setIsIndicator(true);
    }

    @Override
    public int getItemCount() {
        if (reviews != null)
            return reviews.size();
        else return 0;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rating_name)
        TextView name;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
