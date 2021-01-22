package com.monresto.acidlabs.monresto.UI.FAQ;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.FAQ;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQRecyclerViewAdapter extends RecyclerView.Adapter<FAQRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FAQ> faqList;

    public FAQRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_faq, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FAQ faq;
        if (faqList != null && !faqList.isEmpty()) {
            faq = faqList.get(i);
            viewHolder.textQuestionFaq.setText(faq.getQuestion());
            viewHolder.containerFaqItem.setOnClickListener(e -> {
                Intent intent = new Intent(context, FAQAnswerActivity.class);
                intent.putExtra("answer", faq.getAnswer());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (faqList == null)
            return 0;
        return faqList.size();
    }

    public void setFAQ(ArrayList<FAQ> list) {
        this.faqList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textQuestionFaq)
        TextView textQuestionFaq;
        @BindView(R.id.containerFaqItem)
        ConstraintLayout containerFaqItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
