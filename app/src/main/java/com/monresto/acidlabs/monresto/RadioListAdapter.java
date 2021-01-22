package com.monresto.acidlabs.monresto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.ViewHolder> {

    private ArrayList<CharSequence> options;
    private ArrayList<CharSequence> descriptionList;
    private Context context;
    private int selectedItem;
    private CharSequence selectedOption;
    private int disabledItem = -1;
    private OnActionPerformed<Integer> onActionPerformed;

    public RadioListAdapter(ArrayList<CharSequence> options, ArrayList<CharSequence> descriptionList, Context context, int selectedItem) {
        this.context = context;
        this.options = options;
        this.descriptionList = descriptionList;
        this.selectedItem = selectedItem;
        selectedOption = "";
    }

    public RadioListAdapter(ArrayList<CharSequence> options, ArrayList<CharSequence> descriptionList, Context context, int selectedItem, int disabledItem) {
        this.context = context;
        this.options = options;
        this.descriptionList = descriptionList;
        this.selectedItem = selectedItem;
        this.disabledItem = disabledItem;
        selectedOption = "";
    }

    public RadioListAdapter(ArrayList<CharSequence> options, Context context, int selectedItem) {
        this.context = context;
        this.options = options;
        this.selectedItem = selectedItem;
        selectedOption = "";
    }

    public RadioListAdapter(ArrayList<CharSequence> options, Context context, int selectedItem, int disabledItem) {
        this.context = context;
        this.options = options;
        this.selectedItem = selectedItem;
        this.disabledItem = disabledItem;
        selectedOption = "";
    }

    public int getSelectedItem() {
        return selectedItem + 1;
    }

    public String getSelectedLabel() {
        try {
            return options.get(selectedItem).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public CharSequence getSelectedOption() {
        return selectedOption;
    }

    @NonNull
    @Override
    public RadioListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_radio_option, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioListAdapter.ViewHolder viewHolder, int i) {
        CharSequence option = options.get(i);
        if (descriptionList != null) {
            CharSequence description = descriptionList.get(i);
            viewHolder.optionDescription.setText(description);
            viewHolder.optionDescription.setVisibility(View.VISIBLE);
        } else {
            viewHolder.optionDescription.setVisibility(View.GONE);
        }

        viewHolder.optionTitle.setText(option);

        if (i == disabledItem) {
            viewHolder.option_radio.setEnabled(false);
        }
        if (i == selectedItem) {
            viewHolder.option_radio.setChecked(true);
            selectedOption = viewHolder.optionTitle.getText();
        } else viewHolder.option_radio.setChecked(false);

        viewHolder.option_container.setOnClickListener(v -> {
            selectedItem = i;
            selectedOption = viewHolder.optionTitle.getText();
            notifyDataSetChanged();
            if (onActionPerformed != null) onActionPerformed.onPerform(i);
        });
        viewHolder.option_radio.setOnClickListener(v -> {
            selectedItem = i;
            selectedOption = viewHolder.optionTitle.getText();
            notifyDataSetChanged();
            if (onActionPerformed != null) onActionPerformed.onPerform(i);
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public void setOnActionPerformed(OnActionPerformed<Integer> onActionPerformed) {
        this.onActionPerformed = onActionPerformed;
    }

    public void setText(int i, String text) {
        options.set(i, text);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_container)
        View option_container;
        @BindView(R.id.option_title)
        TextView optionTitle;
        @BindView(R.id.option_description)
        TextView optionDescription;
        @BindView(R.id.option_radio)
        RadioButton option_radio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}