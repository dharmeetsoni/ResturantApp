package com.stockde.resturantapp.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.stockde.resturantapp.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import im.delight.android.webview.AdvancedWebView;

//import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends Activity {

    private AdvancedWebView webView;
    private String url_load;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        //............................................

        url_load = "http://ecom.stockde.com/";
        //............................................
        if (url_load != null && !url_load.isEmpty())
        //if (false)
        {
            webViewClient c = new webViewClient(this);
            webView = findViewById(R.id.webView);
            webView.setWebViewClient(c);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });
            webView.setMixedContentAllowed(false);

            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            //PRINT-----------------------------------------
            //Call Function from javascript

            //PRINT-----------------------------------------
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            //webView.loadUrl("http://www.stockde.com");
            webView.addJavascriptInterface(new WebAppInterface(this), "PrnAnd");

            webView.loadUrl(url_load);

        } else {
            //Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            //startActivity(intent);
        }
        //............................................
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @JavascriptInterface
        public void showToast(String toast) {

            //createWebPrintJob(toast);
            doWebViewPrint(toast);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @JavascriptInterface
        public void doWebViewPrint(String htmlStr) {
            try {

                runOnUiThread(() -> {

                    String OldUrl1 = webView.getUrl();
                    webView.loadUrl("about:blank");
                    new Handler().postDelayed(() -> runOnUiThread(() -> {

                        webView.loadDataWithBaseURL(null, htmlStr, "text/HTML", "UTF-8", null);
                        doPrint(OldUrl1);
                    }), 100);

                });

                /*    *//* runOnUiThread(() -> doPrint(webView.getUrl(),htmlStr));*//*
                // Create a WebView object specifically for printing
                Toast.makeText(mContext, "1", Toast.LENGTH_SHORT).show();
                AdvancedWebView web_View;
                web_View = findViewById(R.id.webView);
                Toast.makeText(mContext, "2", Toast.LENGTH_SHORT).show();
                web_View.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Toast.makeText(mContext, "3", Toast.LENGTH_SHORT).show();
                        createWebPrintJob(view);
                        Toast.makeText(mContext, "4", Toast.LENGTH_SHORT).show();
                        webView = null;
                    }
                });

                // Generate an HTML document on the fly:
                Toast.makeText(mContext, "5", Toast.LENGTH_SHORT).show();
                String htmlDocument = htmlStr;
                Toast.makeText(mContext, "6", Toast.LENGTH_SHORT).show();


                // Keep a reference to WebView object until you pass the PrintDocumentAdapter
                // to the PrintManager
                Toast.makeText(mContext, "7", Toast.LENGTH_SHORT).show();
                webView = web_View;
                Toast.makeText(mContext, "done", Toast.LENGTH_SHORT).show();*/
                //createWebPrintJob(textView);
                //--------------------------------------------------------
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_main);
//                // init webView
//                webView = (WebView) findViewById(R.id.simpleWebView);
//                // displaying text in WebView
//                webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
//                createWebPrintJob(webView);
//            }
                //--------------------------------------------------------

            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void createWebPrintJob(WebView webView) {

            PrintManager printManager = (PrintManager) MainActivity.this.getSystemService(Context.PRINT_SERVICE);

            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("MyDocument");

            String jobName = getString(R.string.app_name) + " Print Test";

            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    private void doPrint(String OldUrl1) {

        // Get the print manager.
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Create a wrapper PrintDocumentAdapter to clean up when done.
        PrintDocumentAdapter adapter = new PrintDocumentAdapter() {
            private final PrintDocumentAdapter mWrappedInstance = webView.createPrintDocumentAdapter();

            @Override
            public void onStart() {
                mWrappedInstance.onStart();

            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                mWrappedInstance.onLayout(oldAttributes, newAttributes, cancellationSignal, callback, extras);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

                Handler handler = new Handler();
                handler.postDelayed(
                        () -> mWrappedInstance.onWrite(pages, destination, cancellationSignal, callback), 1000L);

            }

            @Override
            public void onFinish() {
                webView.loadUrl(OldUrl1);
                mWrappedInstance.onFinish();
                // Intercept the finish call to know when printing is done
                // and destroy the WebView as it is expensive to keep around.
               /* webView.destroy();
                webView = null;*/
            }
        };


        printManager.print("MotoGP stats", adapter, null);

    }

    class webViewClient extends WebViewClient {

        private Activity activity;

        public webViewClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

}