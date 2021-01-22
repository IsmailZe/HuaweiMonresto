package com.monresto.acidlabs.monresto.UI.Homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.CategoryIds;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.PlacePickerDialog;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Homepage.Snacks.SnacksActivity;
import com.monresto.acidlabs.monresto.UI.Main.MainActivity;
import com.monresto.acidlabs.monresto.UI.User.SelectAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.monresto.acidlabs.monresto.Model.Semsem.loginPending;


public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {


    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context context;
    private ArrayList<HomeItem> homeItems;

    public HomeListAdapter(Context context, ArrayList<HomeItem> homeItems) {
        this.context = context;
        this.homeItems = homeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_list_home, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HomeItem homeItem = homeItems.get(i);

        if (homeItem.getIcon() != null) {
            viewHolder.nivItem.setVisibility(View.VISIBLE);
            Picasso.get().load(homeItem.getIcon()).into(viewHolder.nivItem);
        } else {
            viewHolder.nivItem.setVisibility(View.GONE);
        }

        if (homeItem.getIndication() != null) {
            viewHolder.tvIndication.setVisibility(View.VISIBLE);
            viewHolder.tvIndication.setText(homeItem.getIndication());
        } else viewHolder.tvIndication.setVisibility(View.GONE);

        if (homeItem.getName() != null) {
            viewHolder.tvLabel.setVisibility(View.VISIBLE);
            viewHolder.tvLabel.setText(homeItem.getName());
        } else viewHolder.tvLabel.setVisibility(View.GONE);

        if (homeItem.getDescription() != null) {
            viewHolder.tvDescription.setVisibility(View.VISIBLE);
            viewHolder.tvDescription.setText(homeItem.getDescription());
        } else viewHolder.tvDescription.setVisibility(View.GONE);


        switch (homeItem.clickToAction) {

            case "538":

                viewHolder.itemView.setOnClickListener(e -> {
                    if (Utilities.isNetworkAvailable(context)) {
                        if (!ShoppingCart.getInstance().isEmpty() && !(ShoppingCart.getInstance().getRestoID() == CategoryIds.SUPERMARCHE || ShoppingCart.getInstance().getRestoID() == 0)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Vider le panier et changer de Supermarché ?");

                            builder.setPositiveButton("OK", (dialog, which) -> {
                                ShoppingCart.getInstance().clear();
                                SharedPreferences sharedPreferences = context.getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(context, SnacksActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", CategoryIds.SUPERMARCHE);
                                context.startActivity(intent);
                            });
                            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

                            AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(arg0 -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#33b998"));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#33b998"));
                            });
                            dialog.show();
                        } else {
                            Intent intent = new Intent(context, SnacksActivity.class);
                            intent.putExtra("EXTRA_SESSION_ID", CategoryIds.SUPERMARCHE);
                            context.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case "543":
                // if traiteur was selected
                viewHolder.itemView.setOnClickListener(e -> {
                    if (Utilities.isNetworkAvailable(context)) {
                        if (!ShoppingCart.getInstance().isEmpty() && !(ShoppingCart.getInstance().getRestoID() == CategoryIds.TRAITEUR || ShoppingCart.getInstance().getRestoID() == 0)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Vider le panier et changer de Traiteur ?");

                            builder.setPositiveButton("OK", (dialog, which) -> {
                                ShoppingCart.getInstance().clear();
                                SharedPreferences sharedPreferences = context.getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(context, SnacksActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", CategoryIds.TRAITEUR);
                                context.startActivity(intent);
                            });
                            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

                            AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(arg0 -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#33b998"));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#33b998"));
                            });
                            dialog.show();
                        } else {
                            Intent intent = new Intent(context, SnacksActivity.class);
                            intent.putExtra("EXTRA_SESSION_ID", CategoryIds.TRAITEUR);
                            context.startActivity(intent);
                        }

                    } else {
                        Toast.makeText(context, context.getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            default:
                viewHolder.itemView.setOnClickListener(e -> {
                    if (Utilities.isNetworkAvailable(context)) {

                        if (homeItem.clickToAction != null && !homeItem.clickToAction.equals("null")) {
                            if (!ShoppingCart.getInstance().isEmpty() && !(ShoppingCart.getInstance().getRestoID() == Integer.parseInt(homeItem.clickToAction) || ShoppingCart.getInstance().getRestoID() == 0)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Vider le panier et changer de Traiteur ?");

                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    ShoppingCart.getInstance().clear();
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent intent = new Intent(context, SnacksActivity.class);
                                    intent.putExtra("EXTRA_SESSION_ID", Integer.parseInt(homeItem.clickToAction));
                                    context.startActivity(intent);
                                });
                                builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

                                AlertDialog dialog = builder.create();
                                dialog.setOnShowListener(arg0 -> {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#33b998"));
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#33b998"));
                                });
                                dialog.show();
                            } else {
                                Intent intent = new Intent(context, SnacksActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", Integer.parseInt(homeItem.clickToAction));
                                context.startActivity(intent);
                            }
                        } /*else if (homeItem.serviceType != null && !homeItem.serviceType.equals("null")) {
                            openRestaurantByServiceType(homeItem.serviceType);
                        }*/ else {
                            if (loginPending) {
                                Toast.makeText(context, "Connexion en cours.. Veuillez patienter", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Intent intent;
                            if (User.getInstance() == null) {
                                PlacePickerDialog placePickerDialog = new PlacePickerDialog();

                                placePickerDialog.setOnLocationSelected(
                                        latLng -> {
                                            Semsem.setLat(latLng.latitude);
                                            Semsem.setLon(latLng.longitude);
                                            Intent intent1 = new Intent(context, MainActivity.class);
                                            context.startActivity(intent1);
                                        });
                                placePickerDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
                                /*todo replace place picker
                                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                                try {
                                    intent = builder.build((AppCompatActivity) context);
                                    intent.putExtra("EXTRA_SESSION_ID", homeItem.serviceType);
                                    ((AppCompatActivity) context).startActivityForResult(intent, Config.REQUEST_PLACE_PICKER);
                                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e1) {
                                    e1.printStackTrace();
                                }*/
                            } else {
                                intent = new Intent(context, SelectAddressActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", homeItem.serviceType);
                                ((AppCompatActivity) context).startActivityForResult(intent, Config.REQUEST_CODE_ADRESS_SELECT);
                            }


                        }

                    } else {
                        Toast.makeText(context, context.getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (homeItems == null)
            return 0;
        return homeItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item)
        ImageView nivItem;
        @BindView(R.id.tv_label)
        TextView tvLabel;
        @BindView(R.id.tv_indication)
        TextView tvIndication;
        @BindView(R.id.tv_description)
        TextView tvDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
