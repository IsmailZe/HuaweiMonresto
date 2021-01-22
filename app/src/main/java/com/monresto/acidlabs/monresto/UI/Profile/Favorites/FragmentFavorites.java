package com.monresto.acidlabs.monresto.UI.Profile.Favorites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.Utilities;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentFavorites extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.currentStatus)
    ConstraintLayout currentStatus;
    @BindView(R.id.swiper)
    SwipeRefreshLayout swiper;

    DishesRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile_favorites, container, false);
        ButterKnife.bind(this, root);

        swiper.setOnRefreshListener(this);

        adapter = new DishesRecyclerViewAdapter(getContext());
        return root;
    }

    public void updateList(ArrayList<Dish> dishes) {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            adapter.setDishes(dishes);
            adapter.notifyDataSetChanged();

            if (dishes.isEmpty())
                try {
                    Utilities.statusChangerUnavailable(Objects.requireNonNull(getActivity()), "La liste de favoris est vide", currentStatus, swiper);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "La liste de favoris est vide", Toast.LENGTH_SHORT).show();
                }
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
        userService.getFavoritesDishes(User.getInstance().getId());
        swiper.setRefreshing(false);
    }
}
