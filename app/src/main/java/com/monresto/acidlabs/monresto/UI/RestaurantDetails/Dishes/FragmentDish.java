package com.monresto.acidlabs.monresto.UI.RestaurantDetails.Dishes;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.RestaurantDetailsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("ValidFragment")
public class FragmentDish extends Fragment {

    private static final String ARG_PARAM = "param";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ArrayList<Dish> dishes;


    public static FragmentDish newInstance(ArrayList<Dish> dishes) {
        FragmentDish fragment = new FragmentDish();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, dishes);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.restaurant_content, null);
        if (getArguments() != null) {
            dishes = getArguments().getParcelableArrayList(ARG_PARAM);
        }
        setUpView(root);

        return root;
    }

    void setUpView(ViewGroup root) {
        ButterKnife.bind(this, root);
        setUPList();
    }

    void setUPList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RestaurantDetailsAdapter adapter = new RestaurantDetailsAdapter(createItemList(), this.getContext());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Dish> createItemList() {
        ArrayList<Dish> dishesList = new ArrayList<>();
        if (dishes.size() > 0)
            dishesList.addAll(dishes);
        return dishesList;
    }

}
