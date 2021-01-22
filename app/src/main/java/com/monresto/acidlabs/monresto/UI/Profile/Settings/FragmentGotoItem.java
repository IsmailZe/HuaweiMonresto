package com.monresto.acidlabs.monresto.UI.Profile.Settings;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentGotoItem extends Fragment {
    @BindView(R.id.profileSettingLabel)
    TextView label;
    @BindView(R.id.profileSettingIcon)
    ImageView icon;
    @BindView(R.id.itemProfileSetting)
    ConstraintLayout constraintLayout;

    private String _label;
    private Drawable _drawable;
    private Class _class;
    private View.OnClickListener _listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.item_profile_setting, container, false);
        ButterKnife.bind(this, root);

        if (_label != null)
            label.setText(_label);
        if (_drawable != null)
            icon.setImageDrawable(_drawable);
        if (_class != null) {
            Intent intent = new Intent(getActivity(), _class);
            constraintLayout.setOnClickListener(e -> startActivity(intent));
        }
        if (_listener != null) {
            constraintLayout.setOnClickListener(_listener);
        }

        return root;
    }

    public FragmentGotoItem setLabel(String s) {
        _label = s;
        return this;
    }

    public FragmentGotoItem setIcon(Drawable drawable) {
        _drawable = drawable;
        return this;
    }

    public FragmentGotoItem setIntent(Class activity) {
        _class = activity;
        return this;
    }

    public FragmentGotoItem setAction(View.OnClickListener listener) {
        _listener = listener;
        return this;
    }
}
