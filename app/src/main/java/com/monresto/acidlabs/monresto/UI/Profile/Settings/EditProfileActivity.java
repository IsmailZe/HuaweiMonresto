package com.monresto.acidlabs.monresto.UI.Profile.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity implements UserAsyncResponse {
    @BindView(R.id.buttonBack)
    ImageView buttonBack;
    @BindView(R.id.textFname)
    TextView textFname;
    @BindView(R.id.textLname)
    TextView textLname;
    @BindView(R.id.textEmail)
    TextView textEmail;
    @BindView(R.id.textPhoneNumber)
    TextView textPhoneNumber;
    @BindView(R.id.buttonSubmit)
    TextView buttonSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings_edit);
        ButterKnife.bind(this);

        User user = User.getInstance();
        if (User.getInstance() != null) {
            textFname.setText(user.getFname());
            textLname.setText(user.getLname());
            textEmail.setText(user.getEmail());
            if (!TextUtils.isEmpty(user.getMobile())) {
                textPhoneNumber.setText(user.getMobile());
            }
        }


        if (User.getInstance() != null) {
            if (!TextUtils.isEmpty(User.getInstance().getMobile()) && User.getInstance().getMobile().length() != 8) {

                buttonBack.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Veuillez ajouter un numero de téléphone", Toast.LENGTH_SHORT).show();

            }
        }
        buttonBack.setOnClickListener(e -> finish());
        buttonSubmit.setOnClickListener(e -> {
            if (verif()) {
                user.setFname(textFname.getText().toString());
                user.setLname(textLname.getText().toString());
                user.setEmail(textEmail.getText().toString());
                user.setMobile(textPhoneNumber.getText().toString());
                SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String savedLogin = sharedPref.getString("passwordLogin", null);
                if (savedLogin == null) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("fbLogin", "{id: " + user.getId() + ", email: " + user.getEmail() + ", fname: " + user.getFname() + ", lname: " + user.getLname() + ", mobile: " + user.getMobile() + "}");
                    editor.apply();
                }
                UserService userService = new UserService(this);
                userService.updateProfile(user);
            }
        });
    }

    private boolean verif() {
        if (TextUtils.isEmpty(textFname.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Veuillez renseigner votre nom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(textLname.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Veuillez renseigner votre prénom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(textEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Veuillez renseigner votre adresse email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Utilities.isValidEmail(textEmail.getText())) {
            Toast.makeText(getApplicationContext(), "Veuillez renseigner une adresse email valide", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(textPhoneNumber.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Veuillez ajouter un numero de téléphone", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!textPhoneNumber.getText().toString().matches("\\d+(?:\\.\\d+)?") || textPhoneNumber.getText().toString().length() != 8) {
            Toast.makeText(getApplicationContext(), "Veuillez renseigner un numero de téléphone valide", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onUserProfileUpdated() {
        Toast.makeText(this, "Vos données ont été mis a jour.", Toast.LENGTH_SHORT).show();
        User.getInstance().setPhone(textPhoneNumber.getText().toString());
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(User.getInstance().getMobile())) {
            if (User.getInstance().getMobile().length() > 4) {
                super.onBackPressed();
            }
        }
    }
}
