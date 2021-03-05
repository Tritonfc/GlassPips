package com.osinachi.glasspips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar, prog;

    private SwipeRefreshLayout swipeRefreshLayout;
    boolean loadingFinished = true;
    boolean redirect = false;

    private String url = "https://www.glasspips.com/dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progBar);
        prog = findViewById(R.id.prog);

        //Initialising webView from the xml File
        webView = findViewById(R.id.myWebView);
        webView.setWebViewClient(new WebViewClient());
        //Tells the webView to enable JavaScript execution
        webView.getSettings().setJavaScriptEnabled(true);
        //Sets whether the DOM storage api is enabled to use system storage functions
        webView.getSettings().setDomStorageEnabled(true);
        webView.setOverScrollMode(webView.OVER_SCROLL_NEVER);

        webView.loadUrl(url);


        //Swipe to refresh
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                webView.reload();


            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap icon) {
                if (swipeRefreshLayout.isRefreshing()) {

                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    prog.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prog.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest resourceRequest, WebResourceError webResourceError) {

                if (webResourceError.getDescription().toString().equals("net::ERR_INTERNET_DISCONNECTED")) {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }

            }


        });


    }


    /*Overrides the onBackPressed method and checks if there's a previous webpage
    before exiting the app
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public Boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Network info = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                info = cm.getActiveNetwork();
                if (info == null) return false;
                NetworkCapabilities actNw = cm.getNetworkCapabilities(info);
                return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
            } else {
                NetworkInfo nwInfo = cm.getActiveNetworkInfo();
                return nwInfo != null && nwInfo.isConnected();
            }

        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}