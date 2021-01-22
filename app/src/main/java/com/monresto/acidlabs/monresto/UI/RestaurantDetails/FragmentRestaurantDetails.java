package com.monresto.acidlabs.monresto.UI.RestaurantDetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.Review;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Review.ReviewAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Review.ReviewService;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.Reviews.ReviewsAdapter;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRestaurantDetails extends Fragment implements ReviewAsyncResponse {

    @BindView(R.id.dish_bg_id)
    ImageView dish_bg;
    @BindView(R.id.rating_id)
    TextView rating;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.delivery_id)
    TextView delivery_price;

    RecyclerView listReviews;
    ConstraintLayout reviewsStatus;

    Restaurant restaurant;
    ReviewService reviewService;
    ReviewsAdapter reviewsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        ButterKnife.bind(this, v);
        listReviews = v.findViewById(R.id.listReviews);
        reviewsStatus = v.findViewById(R.id.reviewsStatus);

        if (getArguments() != null) {
            restaurant = (Restaurant) getArguments().get("restaurant");
        }

        // Preparing reviews recyclerView
        reviewsAdapter = new ReviewsAdapter(getActivity());
        listReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        listReviews.setAdapter(reviewsAdapter);

        // Reviews service call
        reviewService = new ReviewService(this);
        reviewService.getAll(restaurant.getId());

        // Assigning values
        Picasso.get().load(restaurant.getImage()).into(dish_bg);

        ratingBar.setRating((float) restaurant.getRate());
        ratingBar.setIsIndicator(true);

        rating.setText("(" + restaurant.getNbrAvis() + " avis)");
        delivery_price.setText(restaurant.getEstimatedTime() + " minutes | " + Double.toString(restaurant.getDeliveryCost()) + " DT");

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReviewsReceived(ArrayList<Review> ReviewList) {
        if (ReviewList.isEmpty() && getContext() != null)
            Utilities.statusChangerUnavailable(getContext(), "Il n'y a aucun avis", reviewsStatus, listReviews);
        else {
            reviewsStatus.setVisibility(View.INVISIBLE);
            listReviews.setVisibility(View.VISIBLE);

            reviewsAdapter.setReviews(ReviewList);
            reviewsAdapter.notifyDataSetChanged();
        }
    }
}
