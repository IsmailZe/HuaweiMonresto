package com.monresto.acidlabs.monresto.UI.User.RegisterFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRegisterLoginInfo extends Fragment {
    @BindView(R.id.textLogin)
    EditText textLogin;
    @BindView(R.id.textEmail)
    EditText textEmail;
    @BindView(R.id.textPassword)
    EditText textPassword;
    @BindView(R.id.textPasswordConfirm)
    EditText textPasswordConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_register_login_info, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    public int validate() {
        try {
            if (textLogin.getText().toString().equals("") || textPassword.getText().toString().equals("") || textLogin.getText().toString().equals(""))
                return -1;
            if (!textPassword.getText().toString().equals(textPasswordConfirm.getText().toString()))
                return -2;
            if (!Utilities.isValidEmail(textEmail.getText()))
                return -3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public User fill(User user) {
        user.setLogin(textLogin.getText().toString());
        user.setEmail(textEmail.getText().toString());
        user.setPassword(textPassword.getText().toString());
        return user;
    }
}
