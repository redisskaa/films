package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.jsoup.R;
import com.example.jsoup.video.VideoEnabledWebChromeClient;
import com.example.jsoup.video.VideoEnabledWebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FilmActivity extends Activity {
    String res;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            WindowManager.LayoutParams attrs = getWindow().getAttributes();

            if (fullscreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            } else {
                finish();
//                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                getWindow().setAttributes(attrs);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        });

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new ExampleActivity.InsideWebViewClient());
    }

    @SuppressLint("StaticFieldLeak")
    public class Connect extends AsyncTask<String, Integer, String> {

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected String doInBackground(String... strings) {

            try {

                Document doc = Jsoup.connect(strings[0]).get();

                for (Element element1 : doc.select("div#dle-content").select("div.fplayer")) {
                    res = element1.select("iframe").attr("src"); /// Извлечение ссылки из src
                    ///res = element1.html(); /// весь код iframe
                    ///res = doc.html(); /// весь код страницы из url

//                    res = "<iframe " + "src=\"" + res + "\" " + "allow=\"autoplay *; fullscreen\" " + "width=\"640\" " + "height=\"360\" " +
//                            "allowfullscreen=\"\" " +
//                            "webkitallowfullscreen=\"\" " +
//                            "mozallowfullscreen=\"\" " +
//                            "oallowfullscreen=\"\" " +
//                            "msallowfullscreen=\"\">" +
//                            "</iframe>";
                    ///System.out.println(res);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {
//            webView.loadData(res, "text/html", "UTF-8");
            webView.loadUrl(res);
            System.out.println("onPostExecute: " + res);
            super.onPostExecute(res);
        }

        @Override
        protected void onPreExecute() {
            fullScreenWebView();
            Toast.makeText(getApplicationContext(), "Войдите в полноэкранный режим", Toast.LENGTH_SHORT).show();
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