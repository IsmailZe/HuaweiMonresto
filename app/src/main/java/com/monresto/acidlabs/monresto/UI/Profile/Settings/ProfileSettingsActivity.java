package com.monresto.acidlabs.monresto.UI.Profile.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.monresto.acidlabs.monresto.BuildConfig;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileSettingsActivity extends AppCompatActivity {
    @BindView(R.id.imageCloseProfileSettings)
    ImageView imageClose;
    @BindView(R.id.linearLayoutProfileSettings)
    LinearLayout linearLayout;
    @BindView(R.id.textProfileTitle)
    TextView textProfileTitle;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        ButterKnife.bind(this);

        imageClose.setOnClickListener(e -> finish());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof FragmentGotoItem) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }


        UserService userService = new UserService(this);

        fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Informations du profil").setIcon(getResources().getDrawable(R.drawable.ic_avatar_black, getTheme())).setIntent(EditProfileActivity.class));
        fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Mes adresses").setIcon(getResources().getDrawable(R.drawable.icon_address, getTheme())).setIntent(AddressSettingsActivity.class));
        /*fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Promotions").setIcon(getResources().getDrawable(R.drawable.discount_50, getTheme())));*/
        /*fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Bons de réduction").setIcon(getResources().getDrawable(R.drawable.cutting_coupon, getTheme())));*/
        /*fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("FAQ").setIcon(getResources().getDrawable(R.drawable.faq_50, getTheme())).setIntent(FAQActivity.class));*/
        fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Partages").setIcon(getResources().getDrawable(R.drawable.share, getTheme())).setAction(e -> {
            shareApp();
        }));
        /*fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("A propos").setIcon(getResources().getDrawable(R.drawable.info_50, getTheme())));*/
        fragmentTransaction.add(R.id.linearLayoutProfileSettings, new FragmentGotoItem().setLabel("Se déconnecter").setIcon(getResources().getDrawable(R.drawable.log_out, getTheme())).setAction(e -> {
            userService.logout();
            finish();
        }));

        fragmentTransaction.commit();

        if (!TextUtils.isEmpty(User.getInstance().getFname()) && !TextUtils.isEmpty(User.getInstance().getLname())) {
            textProfileTitle.setText(String.format("%s %s", User.getInstance().getFname(), User.getInstance().getLname()));
        }
    }

    private void shareApp() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Faites profiter vos proches : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Partage"));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(User.getInstance().getFname()) && !TextUtils.isEmpty(User.getInstance().getLname())) {
            textProfileTitle.setText(String.format("%s %s", User.getInstance().getFname(), User.getInstance().getLname()));
        }
    }
}
