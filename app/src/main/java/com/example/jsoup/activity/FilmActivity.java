package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                getWindow().setAttributes(attrs);
//            } else {
//                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                getWindow().setAttributes(attrs);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }

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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
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

                if (res == null){
                    for (Element element1 : doc.select("div#dle-content").select("div.fname")) {
                        //res = element1.select("iframe").attr("src"); /// Извлечение ссылки из src
                        res = element1.text();
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {
//            webView.loadData(res, "text/html", "UTF-8");
            if (res == null){
                System.out.println("onPostExecute: " + null);
                showYesNoDialog();
            }else {
                System.out.println("onPostExecute: " + res);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                webView.loadUrl(res);
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