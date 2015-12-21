package com.floreantpos.v14.mobile.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.floreantpos.v14.mobile.R;

public class FAQ extends StyledBaseActivity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.faq);

        final WebView webView = (WebView) findViewById(R.id.webView);

        String url = "file:///android_asset/faq/faq.html";
        webView.loadUrl(url);

        final ProgressDialog dialog = ProgressDialog.show(FAQ.this, null, "Loading document", true);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
                webView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}

