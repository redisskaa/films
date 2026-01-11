package com.example.jsoup.helpclass.asynctask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jsoup.activity.MainActivity;
import com.example.jsoup.helpclass.NetworkCheck;
import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.model.CardFilm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class FetchDataTask extends AsyncTask<String, Integer, List<CardFilm>> {
    private final CustomAdapter adapter;
    private static final String BASE_DOMAIN = "https://kinotac.org";
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private final boolean isAppend;

    public FetchDataTask(CustomAdapter adapter, ProgressBar pBar, boolean isAppend) {
        this.adapter = adapter;
        this.progressBar = pBar;
        this.isAppend = isAppend;
    }

    @Override
    protected List<CardFilm> doInBackground(String... urls) {
        List<CardFilm> dataList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(urls[0]).userAgent("Mozilla/5.0").get();

            Elements items = document.select("div.th-item");
            for (Element item : items) {
                // Безопасные вызовы с проверкой на null
                String title = getTextOrDefault(item.selectFirst("div.th-desc h2"), "Без названия");
                String url = getAttrOrEmpty(item.selectFirst("a"));
                String descr = item.select(".descriptionnew").text(); // Может быть пустым, но не null
                String ratingText = getTextOrDefault(item.selectFirst(".current-rating"), "0");

                // Изображение — с проверкой на null
                String imageUrl = null;
                Element meta = item.selectFirst("meta[itemprop=image]");
                if (meta != null) {
                    String content = meta.attr("content"); // Теперь безопасно
                    if (!content.isEmpty()) {
                        imageUrl = normalizeImageUrl(content, BASE_DOMAIN);
                    }
                }

                dataList.add(new CardFilm(title, url, descr, imageUrl, ratingText));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<CardFilm> result) {
        Context context = progressBar != null ? progressBar.getContext() : null;



        if (context != null && NetworkCheck.isNetworkConnected(context)) {
            if (isAppend) {
                adapter.addData(result);
            } else {
                adapter.updateData(result);
            }
            adapter.notifyDataSetChanged();
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;
                activity.startRatingLoading(result);

                if (activity.currentPage == 1) {
                    activity.setTitle("Недавно добавленные");

                } else {
                    activity.setTitle("Страница: " + activity.currentPage);
                }
                if (isAppend) {
                    activity.isLoading = false;
                }
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        } else if (context != null) {
            Toast.makeText(context, "Отсутствует интернет", Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(result);
    }

    public static String normalizeImageUrl(String url, String baseDomain) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
            return url;
        }
        url = url.replaceAll("^/+", "");
        return baseDomain + "/" + url;
    }

    // Вспомогательные методы для безопасности
    private String getTextOrDefault(Element element, String defaultValue) {
        return element != null ? element.text() : defaultValue;
    }

    private String getAttrOrEmpty(Element element) {
        return element != null ? element.attr("href") : "";
    }

}