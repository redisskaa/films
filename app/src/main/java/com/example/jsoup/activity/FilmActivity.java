package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.jsoup.R;
import com.example.jsoup.video.VideoEnabledWebChromeClient;
import com.example.jsoup.video.VideoEnabledWebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FilmActivity extends Activity {
    String res = null;
    String url = null;
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        String customUserAgent = getResources().getString(R.string.user_agent);
        webSettings.setUserAgentString(customUserAgent);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        // прячем панель навигации и строку состояния
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        if (url != null) {
            new Connect().execute(url);
        } else {
            Toast.makeText(this, "К сожелению переменная url пуста", Toast.LENGTH_SHORT).show();
        }
    }

    public void fullScreenWebView(){
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }
        };


        webChromeClient.setOnToggledFullscreen(fullscreen -> {

            WindowManager.LayoutParams attrs = getWindow().getAttributes();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if (fullscreen) {
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                } else {
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }

            }


        });

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Автоматически войти в полноэкранный режим для видео
                String js = "(function() { var video = document.getElementsByTagName('video')[0]; if (video) { video.requestFullscreen(); } })()";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(js, null);
                } else {
                    view.loadUrl("javascript:" + js);
                }
            }
        });
    }

    private void showYesNoDialog() {
        AlertDialog.Builder builder = getBuilder();

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Информация");
        builder.setMessage("К сожелению фильм еще не вышел");

        // Кнопка "Да"
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        return builder;
    }

    @SuppressLint("StaticFieldLeak")
    public class Connect extends AsyncTask<String, Integer, String> {

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected String doInBackground(String... strings) {

            try {

                Document doc = Jsoup.connect(strings[0]).get();

                // В doInBackground():
                for (Element element1 : doc.select("div.fplayer")) {
                    Element iframe = element1.selectFirst("iframe");
                    if (iframe != null) {
                        res = iframe.attr("src");
                        break;
                    }
                }

                // Если не найден — fallback
                if (res == null || res.isEmpty()) {
                    Element fname = doc.selectFirst("div#dle-content div.fname");
                    if (fname != null) {
                        res = fname.text(); // или другой fallback
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {

            if (res == null){
                System.out.println("onPostExecute: " + null);
                showYesNoDialog();
            }else {
                System.out.println("onPostExecute: " + res);
                webView.loadUrl("https:" + res);
                //webView.loadData(res, "text/html", "UTF-8");
            }
            super.onPostExecute(res);
        }

        @Override
        protected void onPreExecute() {
            fullScreenWebView();
            super.onPreExecute();
        }
    }

    @Override
    public void onBackPressed() {

        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            }
            else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }

}