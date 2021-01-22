package com.monresto.acidlabs.monresto.UI.Profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProfileLogin extends Fragment {
    @BindView(R.id.userData)
    TextView userData;
    @BindView(R.id.buttonLogout)
    Button buttonLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile_login, container, false);
        ButterKnife.bind(this, root);

        buttonLogout.setOnClickListener(e -> {
            User.setInstance(null);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("passwordLogin");
                editor.remove("fbLogin");
                editor.apply();
            }

            ViewPager currentViewPager = getActivity().findViewById(R.id.viewPagerProfile);
            currentViewPager.setCurrentItem(0);
        });
        return root;
    }

    public void update() {
        userData.setText(User.getInstance().getId() + " - " + User.getInstance().getFname());
    }
}
