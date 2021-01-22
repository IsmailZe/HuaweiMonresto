package com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ComponentsAdapter extends ArrayAdapter<Dish.Option> {

    private ArrayList<Dish.Option> optionsList;
    private Context context;
    private int selectedItemCounter;
    private int maxChoices;
    private TextView total_order;
    private ArrayList<Integer> checkedItemsPositions;
    private TextView dish_quantity;

    public ComponentsAdapter(ArrayList<Dish.Option> optionsList, Context context, int maxChoices, TextView total_order, TextView dish_quantity) {
        super(context, R.layout.item_dish_option, optionsList);
        this.optionsList = optionsList;
        this.context = context;
        selectedItemCounter = 0;
        this.maxChoices = maxChoices;
        this.total_order = total_order;
        checkedItemsPositions = new ArrayList<>();
        this.dish_quantity = dish_quantity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_dish_component, null);

            ButterKnife.bind(this, v);
        }

        final Dish.Option option = optionsList.get(position);

        TextView component_name = v.findViewById(R.id.component_name);
        TextView component_price = v.findViewById(R.id.component_price);
        final CheckBox component_checkbox = v.findViewById(R.id.component_checkbox);

        component_name.setText(option.getTitle());
        component_price.setText("(" + String.valueOf(option.getPrice()) + " DT)");

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                component_checkbox.performClick();
            }
        });
        component_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItemCounter++;
                    if (selectedItemCounter > maxChoices) {
                        buttonView.setChecked(false);
                        selectedItemCounter--;
                        notifyDataSetChanged();
                    } else {
                        total_order.setText(String.valueOf(Double.valueOf(total_order.getText().toString().replace(",", ".")) + option.getPrice() * Integer.valueOf(dish_quantity.getText().toString())));

                        checkedItemsPositions.add(position);

                    }
                } else {
                    selectedItemCounter--;
                    total_order.setText(String.valueOf(Double.valueOf(total_order.getText().toString().replace(",", ".")) - option.getPrice() * Integer.valueOf(dish_quantity.getText().toString())));

                    checkedItemsPositions.remove(checkedItemsPositions.indexOf(position));

                }
            }
        });

        return v;
    }


    public ArrayList<Integer> getCheckedItemsPositions() {
        return checkedItemsPositions;
    }
}
