package com.monresto.acidlabs.monresto.UI.User;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.facebook.appevents.AppEventsConstants;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.City;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.City.CityAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Maps.MapsActivity;
import com.monresto.acidlabs.monresto.UI.Profile.Address.NewAddressActivity;
import com.monresto.acidlabs.monresto.UI.Restaurants.ViewPagerAdapter;
import com.monresto.acidlabs.monresto.UI.User.RegisterFragments.FragmentRegisterAddress;
import com.monresto.acidlabs.monresto.UI.User.RegisterFragments.FragmentRegisterLoginInfo;
import com.monresto.acidlabs.monresto.UI.User.RegisterFragments.FragmentRegisterPersonalInfo;
import com.monresto.acidlabs.monresto.UI.User.RegisterFragments.FragmentRegisterSubmit;
import com.monresto.acidlabs.monresto.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements UserAsyncResponse, CityAsyncResponse {
    @BindView(R.id.viewPagerRegister)
    ViewPager viewPager;
    @BindView(R.id.nextButton)
    Button nextButton;

    private Address address = new Address();
    private Geocoder geocoder;

    private User newUser;

    private FragmentRegisterAddress fragmentRegisterAddress;
    private FragmentRegisterSubmit fragmentRegisterSubmit;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        FragmentRegisterLoginInfo fragmentRegisterLoginInfo = new FragmentRegisterLoginInfo();
        FragmentRegisterPersonalInfo fragmentRegisterPersonalInfo = new FragmentRegisterPersonalInfo();
        fragmentRegisterAddress = new FragmentRegisterAddress();
        fragmentRegisterSubmit = new FragmentRegisterSubmit();

        viewPagerAdapter.AddFragment(fragmentRegisterLoginInfo, "fragmentRegisterLoginInfo");
        viewPagerAdapter.AddFragment(fragmentRegisterPersonalInfo, "fragmentRegisterPersonalInfo");
        viewPagerAdapter.AddFragment(fragmentRegisterAddress, "fragmentRegisterAddress");
        viewPagerAdapter.AddFragment(fragmentRegisterSubmit, "fragmentRegisterSubmit");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3)
                    nextButton.setText(getResources().getString(R.string.validate_btn));
                else
                    nextButton.setText(getResources().getString(R.string.next_btn));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        userService = new UserService(this);
        newUser = new User(0, "", "", "", "");

        geocoder = new Geocoder(this);

        nextButton.setOnClickListener(e -> {
            Utilities.hideKeyboard(this);
            switch (viewPager.getCurrentItem()) {
                case 0:
                    // nextButton.setText("Suivant");
                    switch (fragmentRegisterLoginInfo.validate()) {
                        case 1:
                            if (Utilities.isNetworkAvailable(this)) {
                                fragmentRegisterLoginInfo.fill(newUser);
                                userService.checkLoginDispo(newUser.getLogin(), newUser.getEmail());
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case -1:
                            Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case -2:
                            Toast.makeText(this, getResources().getString(R.string.unmatched_passwords), Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case -3:
                            Toast.makeText(this, getResources().getString(R.string.invalid_email), Toast.LENGTH_SHORT)
                                    .show();
                        default:
                            break;
                    }
                    break;
                case 1:
                    if (fragmentRegisterPersonalInfo.validate()) {
                        fragmentRegisterPersonalInfo.fill(newUser);
                        Intent intent = new Intent(this, MapsActivity.class);
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 2:
                    if (fragmentRegisterAddress.validate()) {
                        address = fragmentRegisterAddress.fill(address);
                        newUser = fragmentRegisterAddress.addComment(newUser);
                        ArrayList<Address> addresses = new ArrayList<>();
                        addresses.add(address);
                        newUser.setAddresses(addresses);
                        fragmentRegisterSubmit.fill(newUser);
                        viewPager.setCurrentItem(3);
                    } else
                        Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (Utilities.isNetworkAvailable(this)) {
                        userService.register(newUser.getLogin(),
                                newUser.getPassword(),
                                newUser.getPassword_confirm(),
                                newUser.getEmail(),
                                newUser.getFname(),
                                newUser.getLname(),
                                newUser.getCivility(),
                                newUser.getPhone(),
                                newUser.getMobile(),
                                newUser.getComment(),
                                newUser.getAddresses());
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                default:
                    break;

            }
        });
    }


    @Override
    public void onUserLogin(User user) {

        Bundle params = new Bundle();

        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "User");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, user.toString());

        Config.logger.logEvent("Subscribe",
                1,
                params);
        User.setInstance(user);
        userService.getAddress(User.getInstance().getId());
    }

    @Override
    public void onUserDetailsReceived(User user) {

    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        if (User.getInstance() != null) {
            User.getInstance().setAddresses(addresses);
            Semsem.loginPending = false;
            if (addresses.isEmpty()) {
                Intent intent = new Intent(this, NewAddressActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }


    @Override
    public void oncheckLoginDispoReceived(boolean isDispo) {
        if (isDispo) {
            viewPager.setCurrentItem(1);
        } else
            Toast.makeText(this, getResources().getString(R.string.login_already_exists), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                viewPager.setCurrentItem(2);

                double lat = data.getDoubleExtra("lat", 36.849109);
                double lon = data.getDoubleExtra("lon", 10.166124);
                android.location.Address currentAddress;
                try {
                    List<android.location.Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                    if (addressList != null && !addressList.isEmpty()) {
                        currentAddress = addressList.get(0);
                        address.setPostalCode(currentAddress.getPostalCode());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                address.setLat(lat);
                address.setLon(lon);
            }
        }
    }

    @Override
    public void onCitiesReceived(ArrayList<City> cities) {
        fragmentRegisterAddress.fillCities(cities);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (viewPager.getCurrentItem()) {
                case 1:
                case 3:
                    viewPager.setCurrentItem(0);
                    break;
                case 2:
                    viewPager.setCurrentItem(1);
                    break;
                default:
                    return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
