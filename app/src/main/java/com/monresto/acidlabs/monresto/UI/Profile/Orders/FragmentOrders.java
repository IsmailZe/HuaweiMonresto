package com.monresto.acidlabs.monresto.UI.Profile.Orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.monresto.acidlabs.monresto.Model.Order;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Profile.ProfileActivity;
import com.monresto.acidlabs.monresto.Utilities;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentOrders extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UserAsyncResponse {
    @BindView(R.id.layoutOrdersContainer)
    RecyclerView recyclerView;
    @BindView(R.id.currentStatus)
    ConstraintLayout currentStatus;
    @BindView(R.id.swiper)
    SwipeRefreshLayout swiper;

    OrderRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile_orders, container, false);
        ButterKnife.bind(this, root);

        swiper.setOnRefreshListener(this);

        adapter = new OrderRecyclerViewAdapter(getContext(), this);

        return root;
    }

    @Override
    public void onCancelOrder(boolean success) {
        ((ProfileActivity) getActivity()).refreshOrders();
        recyclerView.setVisibility(View.GONE);
        currentStatus.setVisibility(View.VISIBLE);
    }

    public void fillPending(ArrayList<Order> orders) {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            adapter.setOrders(orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (orders.isEmpty())
                Utilities.statusChangerUnavailable(Objects.requireNonNull(getContext()), "Vous n'avez pas de commandes", currentStatus, swiper);
            else {
                swiper.setVisibility(View.VISIBLE);
                currentStatus.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRefresh() {
        UserService userService = new UserService(getContext());
        userService.getOrders(User.getInstance().getId());
        swiper.setRefreshing(false);

    }
}
