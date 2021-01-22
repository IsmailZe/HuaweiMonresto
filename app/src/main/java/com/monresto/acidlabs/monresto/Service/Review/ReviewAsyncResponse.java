package com.monresto.acidlabs.monresto.Service.Review;

import com.monresto.acidlabs.monresto.Model.Review;

import java.util.ArrayList;

public interface ReviewAsyncResponse {
    void onReviewsReceived(ArrayList<Review> ReviewList);
}
