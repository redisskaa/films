package com.example.jsoup.helpclass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.R;
import com.example.jsoup.model.CardFilm;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<CardFilm> dataList;
    private final Context context;

    public CustomAdapter(Context context, List<CardFilm> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CardFilm data = dataList.get(position);
        holder.textView.setText(data.getTitle());
        System.out.println("Ссылка:" + data.getUrlImage());

        System.out.println("onBindViewHolder:" + data.getRating());

        String ratingStr = data.getRating();
        int rating = 0;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        if (rating > 0 && rating <= 100) {
            holder.ratingBar.setRating(rating / 20f); // 100 → 5.0
            holder.ratingBar.setVisibility(View.VISIBLE);
        } else {
            System.out.println("К сожелению рейтинг равен 0");
        }

        Picasso.get()
                .load(data.getUrlImage())
                .fit()
                .error(R.drawable.image)
                .placeholder(R.drawable.image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<CardFilm> newData) {
        this.dataList.clear();
        this.dataList.addAll(newData);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.view_image);
            textView = itemView.findViewById(R.id.view_title);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}