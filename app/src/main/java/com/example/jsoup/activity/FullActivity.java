package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jsoup.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FullActivity extends Activity {

    String url, title, url_image, descr;
    TextView textView, view_descr;
    ImageView imgViewFull;
    WebView webView;
    String res = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full);
        getData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void getData(){
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        url_image = intent.getStringExtra("url_image");
        descr = intent.getStringExtra("descr");
        textView = findViewById(R.id.view_title_full);
        imgViewFull = findViewById(R.id.view_image_full);
        view_descr = findViewById(R.id.view_descr);
        new Connect().execute();
        Picasso.get().load(url_image).placeholder(R.drawable.image).into(imgViewFull);
        textView.setText(title);
        view_descr.setText(descr);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    @SuppressLint("StaticFieldLeak")
    public class Connect extends AsyncTask<String, Integer, String>{

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected String doInBackground(String... strings) {

            try {

                Document doc = Jsoup.connect(url).get();

                for (Element element1 : doc.select("div#dle-content").select("div.fplayer")) {
                    res = element1.select("iframe").attr("src");
                    //res = element1.html();
                    res = "<iframe src=\"" + res + "\" allow=\"autoplay *\" width=\"640\" height=\"360\" allowfullscreen=\"true\"></iframe>";
                    System.out.println(res);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            webView.loadData(res, "text/html", "UTF-8");
            System.out.println("onPostExecute: " + res);
            super.onPostExecute(res);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}