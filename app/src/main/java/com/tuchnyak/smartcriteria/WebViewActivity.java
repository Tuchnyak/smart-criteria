package com.tuchnyak.smartcriteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Loads wikipedia web-page article about SMART criteria
 */
public class WebViewActivity extends AppCompatActivity {

    private static final String URL_WIKI_SMART_CRITERIA = "https://en.wikipedia.org/wiki/SMART_criteria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        if (getActionBar() != null)
            getActionBar().hide();

        // initiates webView widget
        WebView webView = findViewById(R.id.webView);

        // webView setup
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL_WIKI_SMART_CRITERIA);

    }

}
