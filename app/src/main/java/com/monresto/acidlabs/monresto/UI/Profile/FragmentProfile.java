package com.monresto.acidlabs.monresto.UI.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Restaurants.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProfile extends Fragment {
    @BindView(R.id.viewPagerProfile)
    ViewPager viewPager;

    FragmentProfileLogin fragmentProfileLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        fragmentProfileLogin = new FragmentProfileLogin();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.AddFragment(new FragmentProfileNoLogin(), "no_login");
        viewPagerAdapter.AddFragment(fragmentProfileLogin, "login");

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(0);
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);

        if ((getFragmentManager() != null) && isVisible) {
            if (User.getInstance() == null)
                viewPager.setCurrentItem(0);
            else {
                viewPager.setCurrentItem(1);
                fragmentProfileLogin.update();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (User.getInstance() == null)
            viewPager.setCurrentItem(0);
        else {
            viewPager.setCurrentItem(1);
            fragmentProfileLogin.update();
        }
    }
}
