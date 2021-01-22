package com.monresto.acidlabs.monresto;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.model.LatLng;
import com.monresto.acidlabs.monresto.Service.GeocodingService;

import java.io.UnsupportedEncodingException;

public class PlacePickerDialog extends DialogFragment {

    private View rootView;
    private HuaweiMap huaweiMap;
    private OnActionPerformed<LatLng> onLocationSelected;
    private OnActionPerformed<String> onAddressFetched;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_place_picker, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {

        View tvChoisir = rootView.findViewById(R.id.tv_choisir);
        MapView mapView = rootView.findViewById(R.id.map_view);
        Bundle mapViewBundle = null;
        mapView.onCreate(mapViewBundle);
        // get map by async method
        mapView.getMapAsync(map -> {
            Log.d("dtseMaptest", "onMapReady: ");
            // after call getMapAsync method ,we can get HuaweiMap instance in this call back method
            huaweiMap = map;
            if (huaweiMap != null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    huaweiMap.setMyLocationEnabled(true);
                }
            }
        });

        tvChoisir.setOnClickListener(view -> {
                    if (onLocationSelected != null)
                        onLocationSelected.onPerform(huaweiMap.getCameraPosition().target);
                    try {
                        if (onAddressFetched != null)
                            GeocodingService.reverseGeocoding(huaweiMap.getCameraPosition().target, object -> {
                                onAddressFetched.onPerform(object);
                            });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
        );
    }

    public void setOnLocationSelected(OnActionPerformed<LatLng> onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    public void setOnAddressFetched(OnActionPerformed<String> onAddressFetched) {
        this.onAddressFetched = onAddressFetched;
    }
}
