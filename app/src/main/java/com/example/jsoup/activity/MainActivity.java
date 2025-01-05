package com.example.jsoup.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ArrayList<CardFilm> dataList;
    private final String url = "https://kinots.org/filmy/";
    CustomAdapter adapter;
    ProgressBar progressBar;
    Button btnNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.pBar);
        btnNextPage = findViewById(R.id.button_next_page);
        adapter = new CustomAdapter(this, dataList);

        ///Адаптер и лист для страниц
        List<String> list = new ArrayList<>();
        PageAdapter pageAdapter = new PageAdapter(this, list);
        ///

        dataList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataList);

        new FetchDataTask(adapter, progressBar).execute(url);

        btnNextPage.setOnClickListener(v -> fetchData());

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

    private static String getRandomString(List<String> list, int minPage, int maxPage) {
        Random random = new Random();
        int index = random.nextInt(maxPage - minPage + 1) + minPage;
        System.out.println("getRandomString: " + index);
        return list.get(index);
    }

    private void fetchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Document document = Jsoup.connect(url).get();

                    UniversalListItem<String> pagesListObj = new UniversalListItem<>();
                    ///UniversalListItem<String> urlsObject = new UniversalListItem<>();

                    for (Element pages : document.select("div.navigation").select("a")) {
                        String page = pages.text(); /// все страницы
                        pagesListObj.addItem(page);
                    }

                    ///System.out.println(pagesListObj.getItems()); /// получение листа с номерами страниц


//                    for (Element pages : document.select("div.navigation").select("a")) {
//                        String attr_href = pages.attr("href"); /// ссылки url на страницы
//                        urlsObject.addItem(attr_href);
//                    }

                    ///System.out.println(urlsObject.getItems()); /// получение листа с url

                    List<String> urlList = new ArrayList<>();
                    List<String> pagesList = pagesListObj.getItems();

                    String minS = pagesList.get(0);
                    String maxS = pagesList.get(pagesList.size() - 1);

                    int min = Integer.parseInt(minS);
                    int max = Integer.parseInt(maxS);

                    for (int i = 0; i < max; i++) {
                        urlList.add(url + "page/" + i + "/");
                    }

                    String randomurl = getRandomString(urlList, min, max);
                    System.out.println("Рандомная страница: " + randomurl);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new FetchDataTask(adapter, progressBar).execute(randomurl);
                        }
                    });
                } catch (Exception e) {
                   e.fillInStackTrace();
                }
            }
        }).start();
    }

}