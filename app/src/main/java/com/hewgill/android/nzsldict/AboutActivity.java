package com.hewgill.android.nzsldict;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends BaseActivity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        setupAppToolbar();
        web = (WebView) findViewById(R.id.about);
        web.loadUrl("file:///android_asset/html/about.html");
    }
}
