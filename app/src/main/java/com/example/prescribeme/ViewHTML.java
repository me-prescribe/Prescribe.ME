package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewHTML extends AppCompatActivity {

    WebView webView;

    String HTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_html);

        Intent HTMLInt=getIntent();
        HTML=HTMLInt.getExtras().getString("HTML","");

        webView=new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL("", HTML, "text/html", "UTF-8", ""); //Displaying Prescription HTML using WebView
        setContentView(webView);
    }
}