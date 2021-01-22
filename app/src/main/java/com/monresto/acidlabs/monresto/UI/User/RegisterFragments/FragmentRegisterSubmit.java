package com.monresto.acidlabs.monresto.UI.User.RegisterFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRegisterSubmit extends Fragment {
    @BindView(R.id.textLoginFinal)
    TextView textLoginFinal;
    @BindView(R.id.textEmailFinal)
    TextView textEmailFinal;
    @BindView(R.id.textFnameFinal)
    TextView textFnameFinal;
    @BindView(R.id.textLnameFinal)
    TextView textLnameFinal;
    @BindView(R.id.textMobileFinal)
    TextView textMobileFinal;
    @BindView(R.id.textFixFinal)
    TextView textFixFinal;
    @BindView(R.id.textAddressFinal)
    TextView textAddressFinal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_register_submit, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    public void fill(User user) {
        textLoginFinal.setText("Login: " + user.getLogin());
        textEmailFinal.setText("Email: " + user.getEmail());
        textFnameFinal.setText("Prénom: " + user.getFname());
        textLnameFinal.setText("Nom: " + user.getLname());
        textMobileFinal.setText("Numéro mobile: " + user.getMobile());
        textFixFinal.setText("Numéro fix: " + user.getPhone());
        Address address = user.getAddresses().get(0);
        textAddressFinal.setText(String.format("Adresse: %s, %s, %s, %s", address.getEmplacement(), address.getAdresse(), address.getRue(), address.getAppartement()));
    }
}
