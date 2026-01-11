// MainActivity.java
package com.example.jsoup.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.R;
import com.example.jsoup.helpclass.NetworkCheck;
import com.example.jsoup.helpclass.RecyclerItemClickListener;
import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.helpclass.asynctask.FetchDataTask;
import com.example.jsoup.model.CardFilm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ArrayList<CardFilm> dataList;
    private final String url = "https://kinotac.org/filmy/";
    CustomAdapter adapter;
    ProgressBar progressBar;
    List<String> list = new ArrayList<>();
    private String useragent;
    RecyclerView recyclerPages;
    private final List<String> listUrls = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public int currentPage = 1;
    private int maxPage = 1;
    public boolean isLoading = false;

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
        // Скрываем пагинацию
        recyclerPages.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Клик по карточке
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CardFilm film = dataList.get(position);
                if (film.getUrlImage() == null || film.getTitle().isEmpty() || film.getUrl().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Данные не загружены", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, FullActivity.class);
                intent.putExtra("url_image", film.getUrlImage());
                intent.putExtra("title", film.getTitle());
                intent.putExtra("url", film.getUrl());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        // Добавляем слушатель скролла для бесконечной прокрутки
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Скролл вниз
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    assert layoutManager != null;
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && currentPage < maxPage &&
                            (firstVisibleItemPosition + visibleItemCount) >= totalItemCount &&
                            firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        currentPage++;
                        String nextUrl = listUrls.get(currentPage - 1);
                        new FetchDataTask(adapter, progressBar, true).execute(nextUrl);
                    }
                }
            }
        });

        if (NetworkCheck.isNetworkConnected(this)) {
            loadPagination();
        } else {
            Toast.makeText(this, "Отсутствует интернет", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorites) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPagination() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).userAgent(useragent).get();
                Elements pageLinks = doc.select("div.navigation a");
                int maxPageLocal = 1;

                if (!pageLinks.isEmpty()) {
                    Element last = pageLinks.last();
                    assert last != null;
                    String text = last.text().trim();
                    try {
                        int parsed = Integer.parseInt(text);
                        if (parsed > 0) maxPageLocal = parsed;
                    } catch (NumberFormatException ignored) {}
                }

                final int finalMaxPage = maxPageLocal;
                runOnUiThread(() -> {
                    maxPage = finalMaxPage;
                    list.clear();
                    listUrls.clear();
                    for (int i = 1; i <= finalMaxPage; i++) {
                        list.add(String.valueOf(i));
                        listUrls.add(url + (i == 1 ? "" : "page/" + i + "/"));
                    }

                    // Загружаем первую страницу
                    new FetchDataTask(adapter, progressBar, false).execute(url);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    maxPage = 1;
                    list.clear();
                    listUrls.clear();
                    list.add("1");
                    listUrls.add(url);
                    Toast.makeText(this, "Ошибка загрузки пагинации", Toast.LENGTH_SHORT).show();
                    new FetchDataTask(adapter, progressBar, false).execute(url);
                });
            }
        }).start();
    }

    public void startRatingLoading(List<CardFilm> films) {
        for (CardFilm film : films) {
            executor.execute(() -> {
                String rating = fetchRating(film.getUrl());
                film.setRating(rating);
                mainHandler.post(() -> {
                    int position = dataList.indexOf(film);
                    if (position != -1) adapter.notifyItemChanged(position);
                });
            });
        }
    }

    private String fetchRating(String filmUrl) {
        try {
            Document doc = Jsoup.connect(filmUrl).userAgent("Mozilla/5.0").timeout(8000).get();
            Element ratingEl = doc.selectFirst("li.current-rating");
            if (ratingEl != null) {
                try {
                    return String.valueOf(Integer.parseInt(ratingEl.text()));
                } catch (Exception e) {
                    return "0";
                }
            }
        } catch (Exception ignored) {}
        return "0";
    }
}