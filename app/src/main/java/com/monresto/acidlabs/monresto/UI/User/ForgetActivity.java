package com.monresto.acidlabs.monresto.UI.User;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.Utilities;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgetActivity extends AppCompatActivity implements UserAsyncResponse {


    @BindView(R.id.textLogin)
    EditText textLogin;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);
        userService = new UserService(this);

        loginButton.setOnClickListener(e -> {
            if (Utilities.isNetworkAvailable(this)) {
                String login = Objects.requireNonNull(textLogin.getText()).toString();
                progressBar2.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                userService.forget(login);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onForgetSuccess(String message) {

        progressBar2.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }
}
