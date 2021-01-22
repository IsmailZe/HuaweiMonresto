package com.monresto.acidlabs.monresto.UI.Homepage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.HomepageEvent;
import com.monresto.acidlabs.monresto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomepageEventsAdapter extends RecyclerView.Adapter<HomepageEventsAdapter.ViewHolder> {

    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context context;
    private ArrayList<HomepageEvent> events;

    public HomepageEventsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HomepageEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_homepage_event, viewGroup, false);

        return new HomepageEventsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomepageEventsAdapter.ViewHolder viewHolder, int i) {
        HomepageEvent event;
        setFadeAnimation(viewHolder.itemView);
        if (events != null && !events.isEmpty()) {
            event = events.get(i);
            viewHolder.item_title.setText(event.getTitle());
            viewHolder.item_label.setText(event.getLabel());
            Picasso.get().load(event.getImage()).into(viewHolder.item_bg);
            Picasso.get().load(event.getRestoIcon()).into(viewHolder.restoBg);
            viewHolder.itemContainer.setOnClickListener(e -> {
                //
                /*Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);*/
            });
        }
    }

    @Override
    public int getItemCount() {
        if (events == null)
            return 0;
        return events.size();
    }

    public void setEvents(ArrayList<HomepageEvent> events) {
        this.events = events;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView item_title;
        @BindView(R.id.item_label)
        TextView item_label;
        @BindView(R.id.item_bg)
        ImageView item_bg;
        @BindView(R.id.restoBg)
        ImageView restoBg;
        @BindView(R.id.itemContainer)
        ConstraintLayout itemContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
