package com.example.jsoup;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.Html;

import com.example.jsoup.model.CardFilm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class FetchDataTask extends AsyncTask<String, Void, List<CardFilm>> {
    private final CustomAdapter adapter;

    public FetchDataTask(CustomAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected List<CardFilm> doInBackground(String... urls) {
        List<CardFilm> dataList = new ArrayList<>();
        try {

            String title = "";
            String image_url = "";
            String url = "";
            String descr = "";

            Document document = Jsoup.connect(urls[0]).get();

            for (Element element : document.select("div.th-item")) {
                title = element.select("div.th-desc").select("h2").text();
                image_url = "https://kinots.org" + element.select("img").attr("src");
                url = element.select("a").attr("href");

                Document docfull = Jsoup.connect(url).get();

                for (Element element1 : docfull.select("div#dle-content")) {
                    descr = element1.select("div.descriptionnew").text();

                    String html = element1.html();
                    Element h2 = Jsoup.parse(html).select("h2").first();

                    if (h2 != null){
                        descr = Html.fromHtml(String.valueOf(h2.nextSibling())).toString();
                    }
                }

                dataList.add(new CardFilm(title, url , descr, image_url));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<CardFilm> result) {
        adapter.updateData(result);
        adapter.notifyDataSetChanged();
        super.onPostExecute(result);
    }
}
