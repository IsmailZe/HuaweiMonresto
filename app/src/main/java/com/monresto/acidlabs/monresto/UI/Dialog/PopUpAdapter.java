package com.monresto.acidlabs.monresto.UI.Dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.PopUp;
import com.monresto.acidlabs.monresto.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PopUpAdapter extends RecyclerView.Adapter<PopUpAdapter.ProductViewHolder> {

    private Activity mCtx;
    private List<PopUp> popUpList;

    public PopUpAdapter(Activity context, List<PopUp> popUpList) {
        this.mCtx = context;
        this.popUpList = popUpList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_popup, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        if (popUpList.get(position).isPay()) {
            holder.popup_txt.setTextColor(mCtx.getResources().getColor(R.color.sweet_dialog_bg_color));
        } else {
            holder.popup_txt.setTextColor(mCtx.getResources().getColor(R.color.black));
        }

        holder.popup_img.setImageDrawable(popUpList.get(position).getPopUpImg());
        holder.popup_txt.setText(popUpList.get(position).getPopUpText());
    }

    @Override
    public int getItemCount() {
        return popUpList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.popup_img)
        ImageView popup_img;
        @BindView(R.id.popup_txt)
        TextView popup_txt;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}