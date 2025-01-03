package com.example.jsoup.helpclass;

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

        int getRate = Integer.parseInt(data.getRating());
        System.out.println("onBindViewHolder:" + getRate);

        switch (getRate){
            case 20:
                holder.ratingBar.setRating(1);
                break;
            case 40:
                holder.ratingBar.setRating(2);
                break;
            case 60:
                holder.ratingBar.setRating(3);
                break;
            case 80:
                holder.ratingBar.setRating(4);
                break;
            case 100:
                holder.ratingBar.setRating(5);
                break;
        }

        Picasso.get().load(data.getUrlImage()).placeholder(R.drawable.image).into(holder.imageView);
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