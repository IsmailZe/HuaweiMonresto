package com.monresto.acidlabs.monresto.UI.FAQ;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQAnswerActivity extends AppCompatActivity {
    @BindView(R.id.textFaqAnswer)
    TextView textFaqAnswer;
    @BindView(R.id.buttonOk)
    Button buttonOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        //int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.40);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_faq_answer);
        ButterKnife.bind(this);
        //getWindow().setLayout(width, height);


        Intent intent = getIntent();
        String answer = intent.getStringExtra("answer");
        textFaqAnswer.setText(answer);
        buttonOk.setOnClickListener(e -> finish());

    }
}
