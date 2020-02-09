package com.test.omdbviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieInfoActivity extends AppCompatActivity {

    private static final String startingURL = "https://www.omdbapi.com/?apikey=9fb86fa6&plot=full";

    private ScrollView mainScrollView;
    private TextView titleTV, subtitleTV, imdbRatingTV, rottenRatingTV, metacriticRatingTV, plotTV, directorTV, writerTV, actorsTV;
    private ImageView posterImageView;
    private ProgressBar movieInfoProgressBar;

    private String title, rated, runtime, genre, released, imdbScore, rottenScore, metacriticScore, plot, director, writer, actors, poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        initiateViews();

        Intent previousActivityIntent = getIntent();
        String movieID = previousActivityIntent.getStringExtra(Movie.IMDB_ID);
        String finalUrl = startingURL + "&i=" + movieID;

        getJsonDataProcess(finalUrl);
    }

    private void initiateViews() {
        mainScrollView = findViewById(R.id.main_scrollview);
        titleTV = findViewById(R.id.movie_title_textView);
        subtitleTV = findViewById(R.id.movie_subtitle_textView);
        imdbRatingTV = findViewById(R.id.imdb_score_textView);
        rottenRatingTV = findViewById(R.id.rotten_score_textView);
        metacriticRatingTV = findViewById(R.id.metacritic_score_textView);
        plotTV = findViewById(R.id.plot_textView);
        directorTV = findViewById(R.id.director_textView);
        writerTV = findViewById(R.id.writer_textView);
        actorsTV = findViewById(R.id.actors_textView);
        posterImageView = findViewById(R.id.movie_poster);
        movieInfoProgressBar = findViewById(R.id.movie_info_progressBar);
    }

    private void getJsonDataProcess(String url) {
        GetJsonTask.ProcessCompletedListener completedListener = new GetJsonTask.ProcessCompletedListener() {
            @Override
            public void onProcessCompleted(String jsonData) {
                parseJSON(jsonData);
            }
        };

        GetJsonTask.ProcessFailedListener failedListener = new GetJsonTask.ProcessFailedListener() {
            @Override
            public void onProcessFailed() {
                movieInfoProgressBar.setVisibility(View.GONE);
                Toast.makeText(MovieInfoActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        };

        new GetJsonTask(completedListener, failedListener).execute(url);
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject movieObject = new JSONObject(jsonData);
            title = movieObject.getString(Movie.TITLE);
            rated = movieObject.getString(Movie.RATED);
            runtime = movieObject.getString(Movie.RUNTIME);
            genre = movieObject.getString(Movie.GENRE);
            released = movieObject.getString(Movie.RELEASED);
            poster = movieObject.getString(Movie.POSTER);
            plot = movieObject.getString(Movie.PLOT);
            director = movieObject.getString(Movie.DIRECTOR);
            writer = movieObject.getString(Movie.WRITER);
            actors = movieObject.getString(Movie.ACTORS);

            JSONArray ratings = movieObject.getJSONArray(Movie.RATINGS);
            for (int i = 0; i < ratings.length(); i++) {
                JSONObject rating = ratings.getJSONObject(i);
                String ratingSource = rating.getString(Movie.SOURCE);
                String ratingValue = rating.getString(Movie.VALUE);

                switch (ratingSource) {
                    case Movie.RATING_IMDB:
                        imdbScore = ratingValue;
                        break;
                    case Movie.RATING_ROTTEN:
                        rottenScore = ratingValue;
                        break;
                    case Movie.RATING_METACRITIC:
                        metacriticScore = ratingValue;
                        break;
                }
            }

            mainScrollView.setVisibility(View.VISIBLE);
            movieInfoProgressBar.setVisibility(View.GONE);
            fetchDataToViews();

        } catch (Exception e) {
            //If there is an error, show a message to the user
            Toast.makeText(this, jsonData, Toast.LENGTH_SHORT).show();
            movieInfoProgressBar.setVisibility(View.GONE);
        }
    }

    private void fetchDataToViews() {
        titleTV.setText(title);
        plotTV.setText(plot);
        directorTV.setText(director);
        writerTV.setText(writer);
        actorsTV.setText(actors);

        StringBuilder subtitle = new StringBuilder();
        subtitle.append(rated).append(" | ").append(runtime).append(" | ").append(genre).append(" | ").append(released);
        subtitleTV.setText(subtitle);

        imdbRatingTV.setText(imdbScore);
        rottenRatingTV.setText(rottenScore);
        metacriticRatingTV.setText(metacriticScore);

        Picasso.get().load(poster).into(posterImageView);
    }
}
