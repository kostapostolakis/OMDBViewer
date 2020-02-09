package com.test.omdbviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    private Context context;
    private ArrayList<Movie> moviesArrayList;

    MoviesAdapter(Context context, ArrayList<Movie> moviesArrayList) {
        super(context, 0, moviesArrayList);
        this.context = context;
        this.moviesArrayList = moviesArrayList;
    }

    @Override
    public int getCount() {
        return moviesArrayList.size();
    }

    @Override
    public Movie getItem(int position) {
        return moviesArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        }

        Movie movie = getItem(position);

        String movieTitle = movie.getTitle();
        String movieYear = movie.getYear();
        String moviePoster = movie.getPoster();

        TextView titleTextView = convertView.findViewById(R.id.title_textView);
        TextView yearTextView = convertView.findViewById(R.id.year_textView);
        ImageView posterImageView = convertView.findViewById(R.id.poster_imageView);

        titleTextView.setText(movieTitle);
        yearTextView.setText(movieYear);
        Picasso.get().load(moviePoster).placeholder(R.color.light_grey).error(R.color.light_grey).into(posterImageView);

        return convertView;
    }
}