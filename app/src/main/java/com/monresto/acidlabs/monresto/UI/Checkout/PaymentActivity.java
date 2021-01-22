package com.monresto.acidlabs.monresto.UI.Checkout;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentActivity extends AppCompatActivity {
    @BindView(R.id.buttonClose)
    ImageView buttonClose;
    @BindView(R.id.webview)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        buttonClose.setOnClickListener(e -> finish());

        int orderID = getIntent().getIntExtra("orderID", 89551);
        String postData = "orderID=" + orderID;

        //webView.postUrl("https://www.monresto.net/processgpg.php", Base64.encode(postData.getBytes()).getBytes());

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<body onload='document.frm1.submit()'>" +
                "<form action='https://www.monresto.net/processgpg.php' method='post' name='frm1'>" +
                "  <input type='hidden' name='orderID' value='" + orderID + "'><br>" +
                "</form>" +
                "</body>" +
                "</html>";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.loadData(html, "text/html", "UTF-8");


    }


}
