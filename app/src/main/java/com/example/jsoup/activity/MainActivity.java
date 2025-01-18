package com.example.jsoup.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.R;
import com.example.jsoup.helpclass.RecyclerItemClickListener;
import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.helpclass.adapters.PageAdapter;
import com.example.jsoup.helpclass.asynctask.FetchDataTask;
import com.example.jsoup.model.CardFilm;
import com.example.jsoup.model.UniversalListItem;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<CardFilm> dataList;
    private final String url = "https://kinots.org/filmy/";
    CustomAdapter adapter;
    ProgressBar progressBar;
    List<String> list = new ArrayList<>();
    private String useragent;
    RecyclerView recyclerPages;
    UniversalListItem<String> urlsObject;
    List<String> listUrls = new ArrayList<>();
    int limitPages = 102;
    int startPage = 1;
    PageAdapter pageAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useragent = getResources().getString(R.string.user_agent);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerPages = findViewById(R.id.recycler);
        progressBar = findViewById(R.id.pBar);
        dataList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataList);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        list = new ArrayList<>();

        for (int i = startPage; i < limitPages; i++) {
            list.add(String.valueOf(i));
            listUrls.add(url + "page/" + i + "/");
        }

        pageAdapter = new PageAdapter(this, list);
        listUrls.add(0, url);
        recyclerPages.setAdapter(pageAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
                String url_image = dataList.get(position).getUrlImage();
                String title = dataList.get(position).getTitle();
                String url = dataList.get(position).getUrl();
                String descr = dataList.get(position).getDescr();

                if (url_image.isEmpty()
                        | title.isEmpty()
                        | url.isEmpty()
                        | descr.isEmpty())
                {
                    System.out.println(url_image + ":" + title + ":" + url + ":" + descr);
                    Toast.makeText(MainActivity.this, "Данные для работы приложения не были получены, попробуйте позже", Toast.LENGTH_SHORT).show();
                }else {
                    //adapter.saveImage(view);
                    intent.putExtra("url_image", url_image);
                    intent.putExtra("title", title);
                    intent.putExtra("url", url);
                    intent.putExtra("descr", descr);
                    startActivity(intent);
                }
            }
            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));

        recycler();
        new FetchDataTask(adapter, progressBar).execute(url);
        setTitle("Недавно добавленные");
        recyclerPages.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPages, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int pos = position + 1;
                String url1 = listUrls.get(pos);
                System.out.println(url1);

                if (pos > 1){
                    setTitle("Страница: " + pos);
                    new FetchDataTask(adapter, progressBar).execute(url1);
                }else {
                    new FetchDataTask(adapter, progressBar).execute(url);
                    setTitle("Недавно добавленные");
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    public String getUseragent() {
        return useragent;
    }

    private void recycler() {
        new Thread(() -> {
            try {

                Document document = Jsoup.connect(url).userAgent(useragent).get();

                UniversalListItem<String> pagesListObj = new UniversalListItem<>();
//                urlsObject = new UniversalListItem<>();

                for (Element pages : document.select("div.navigation").select("a")) {
                    String page = pages.text(); /// все страницы
                    pagesListObj.addItem(page);
                }


//                for (Element pages : document.select("div.navigation").select("a")) {
//                    String attr_href = pages.attr("href"); /// ссылки url на страницы
//                    urlsObject.addItem(attr_href);
//                }

                runOnUiThread(() -> {
                    System.out.println("UI Thread");
                });
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }).start();
    }
}