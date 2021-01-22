package com.monresto.acidlabs.monresto.UI.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.User.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProfileNoLogin extends Fragment {
    @BindView(R.id.buttonGoToLogin)
    Button buttonGoToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_profile_no_login, container, false);

        ButterKnife.bind(this, root);
        buttonGoToLogin.setOnClickListener(e -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        return root;
    }
}
