package com.stockde.resturantapp.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.stockde.resturantapp.ChangeUrl;
import com.stockde.resturantapp.R;
import com.stockde.resturantapp.sharedpref.SharedData;

import im.delight.android.webview.AdvancedWebView;
public class MainActivity extends Activity {

    private AdvancedWebView webView;
    private String url_load;
    private ImageView imgMenu;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        SharedData sharedData = new SharedData(this);
        url_load = sharedData.getUrl();
        imgMenu = findViewById(R.id.imgMenu);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, imgMenu);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast messa  ge on menu item clicked
                        if (menuItem.getItemId() == R.id.changeUrl){
                            Intent intent = new Intent(MainActivity.this, ChangeUrl.class);
                            startActivity(intent);
                            finish();
                        }
                        if (menuItem.getItemId() == R.id.changePrinter){
                            Intent intent = new Intent(MainActivity.this, com.stockde.resturantapp.MainActivity.class);
                            intent.putExtra("isFromChangePrinter",true);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        if (url_load != null && !url_load.isEmpty()) {
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
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.addJavascriptInterface(new WebAppInterface(this), "PrnAnd");
            webView.loadUrl(url_load);

        } else {
            Toast.makeText(this,"Something went wrong, Please contact to admin.", Toast.LENGTH_SHORT).show();
        }
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

            } catch (Exception ex) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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