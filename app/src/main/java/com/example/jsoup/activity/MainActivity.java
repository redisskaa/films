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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useragent = getResources().getString(R.string.user_agent);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerPages = findViewById(R.id.recycler);

        recyclerPages.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPages, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int pos = position + 2;
                setTitle("Страница: " + pos);
                List<String> list1 = urlsObject.getItems();
                String url = list1.get(position);
                System.out.println(url);
                new FetchDataTask(adapter, progressBar).execute(url);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        progressBar = findViewById(R.id.pBar);
        dataList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataList);
        recycler();
        new FetchDataTask(adapter, progressBar).execute(url);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
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

    }

    public String getUseragent() {
        return useragent;
    }

    private void recycler() {
        new Thread(() -> {
            try {

                Document document = Jsoup.connect(url).userAgent(useragent).get();

                UniversalListItem<String> pagesListObj = new UniversalListItem<>();
                urlsObject = new UniversalListItem<>();

                for (Element pages : document.select("div.navigation").select("a")) {
                    String page = pages.text(); /// все страницы
                    pagesListObj.addItem(page);
                }


                for (Element pages : document.select("div.navigation").select("a")) {
                    String attr_href = pages.attr("href"); /// ссылки url на страницы
                    urlsObject.addItem(attr_href);
                }

                ///System.out.println(urlsObject.getItems()); /// получение листа с url

                list = new ArrayList<>(pagesListObj.getItems());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PageAdapter pageAdapter = new PageAdapter(getApplication(), list);
                        recyclerPages.setAdapter(pageAdapter);
                    }
                });
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }).start();
    }
}