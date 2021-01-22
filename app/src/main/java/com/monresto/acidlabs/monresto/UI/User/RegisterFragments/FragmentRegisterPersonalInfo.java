package com.monresto.acidlabs.monresto.UI.User.RegisterFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRegisterPersonalInfo extends Fragment {
    @BindView(R.id.textLastName)
    EditText textLastName;
    @BindView(R.id.textFirstName)
    EditText textFirstName;
    @BindView(R.id.textPhoneNumber)
    EditText textPhoneNumber;
    @BindView(R.id.textMobileNumber)
    EditText textMobileNumber;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_register_personal_info, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    public boolean validate() {
        if (textLastName.getText().toString().equals(""))
            return false;
        if (textFirstName.getText().toString().equals(""))
            return false;
        if (textLastName.getText().toString().equals(""))
            return false;
        if (textPhoneNumber.getText().toString().equals(""))
            return !textMobileNumber.getText().toString().equals("");
        return !textMobileNumber.getText().toString().equals("") || !textPhoneNumber.getText().toString().equals("");

    }

    public User fill(User user) {
        user.setFname(textFirstName.getText().toString());
        user.setLname(textLastName.getText().toString());
        user.setPhone(textPhoneNumber.getText().toString());
        user.setMobile(textMobileNumber.getText().toString());
        return user;
    }
}
