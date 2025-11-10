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

    public FetchDataTask(CustomAdapter adapter, ProgressBar pBar) {
        this.adapter = adapter;
        this.progressBar = pBar;
    }

    @Override
    protected List<CardFilm> doInBackground(String... urls) {
        List<CardFilm> dataList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(urls[0]).userAgent("Mozilla/5.0").get();

            Elements items = document.select("div.th-item");
            for (Element item : items) {
                String title = item.selectFirst("div.th-desc h2") != null ? item.selectFirst("div.th-desc h2").text() : "Без названия";
                String url = item.selectFirst("a") != null ? item.selectFirst("a").attr("href") : "";
                String descr = item.select(".descriptionnew").text(); // Исправлено: поиск по классу
                String ratingText = item.selectFirst(".current-rating") != null ? item.selectFirst(".current-rating").text() : "0";

                // Изображение — внутри текущего th-item
                String imageUrl = null;
                Element meta = item.selectFirst("meta[itemprop=image]");
                if (meta != null) {
                    String content = meta.attr("content");
                    imageUrl = normalizeImageUrl(content, BASE_DOMAIN);
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
            adapter.updateData(result);
            adapter.notifyDataSetChanged();
            if (context instanceof MainActivity) {
                ((MainActivity) context).startRatingLoading(result);
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
}