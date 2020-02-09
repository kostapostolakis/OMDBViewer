package com.test.omdbviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String startingURL = "https://www.omdbapi.com/?apikey=9fb86fa6" + "&type=movie";

    private EditText searchEditText;
    private ImageButton searchButton;
    private TextView emptyListTextView;
    private ListView mainListView;
    private ProgressBar mainProgressBar;
    private ArrayList<Movie> moviesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateViews();
        setClickOfSearchButton();
        setClickOfListItems();
    }

    private void initiateViews() {
        searchEditText = findViewById(R.id.search_editText);
        searchButton = findViewById(R.id.search_button);
        emptyListTextView = findViewById(R.id.empty_list_textView);
        mainListView = findViewById(R.id.main_listView);
        mainProgressBar = findViewById(R.id.main_progressBar);
    }

    private void setClickOfSearchButton() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainListView.setAdapter(null);
                String searchText = searchEditText.getText().toString().trim();

                if (TextUtils.isEmpty(searchText)) {
                    Toast.makeText(MainActivity.this, getString(R.string.no_search_text), Toast.LENGTH_SHORT).show();
                } else {
                    mainProgressBar.setVisibility(View.VISIBLE);
                    String finalURL = startingURL + "&s=" + searchText;
                    getJsonDataProcess(finalURL);
                }
            }
        });
    }

    private void setClickOfListItems() {
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMovieID = moviesArrayList.get(position).getImdbID();
                Intent goToInfoActivityIntent = new Intent(MainActivity.this, MovieInfoActivity.class);
                goToInfoActivityIntent.putExtra(Movie.IMDB_ID, selectedMovieID);
                startActivity(goToInfoActivityIntent);
            }
        });
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
                mainProgressBar.setVisibility(View.GONE);
                emptyListTextView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();

            }
        };

        new GetJsonTask(completedListener, failedListener).execute(url);
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject mainJsonObject = new JSONObject(jsonData);
            JSONArray jsonSearchArray = mainJsonObject.getJSONArray(Movie.SEARCH);

            moviesArrayList = new ArrayList<>();
            for (int i = 0; i < jsonSearchArray.length(); i++) {
                JSONObject movieObject = jsonSearchArray.getJSONObject(i);
                String imdbID = movieObject.getString(Movie.IMDB_ID);
                String title = movieObject.getString(Movie.TITLE);
                String year = movieObject.getString(Movie.YEAR);
                String poster = movieObject.getString(Movie.POSTER);

                Movie movie = new Movie(imdbID, title, year, poster);
                moviesArrayList.add(movie);
            }

            //Show the array to list
            fetchMoviesToList();

        } catch (Exception e) {
            getErrorFromJson(jsonData);
        }
    }

    private void getErrorFromJson(String jsonData) {
        try {
            JSONObject mainJsonObject = new JSONObject(jsonData);
            String jsonError = mainJsonObject.getString(Movie.ERROR);

            if (jsonError.equals(Movie.MOVIE_NOT_FOUND)) {
                emptyListTextView.setText(getString(R.string.no_results));
            } else {
                emptyListTextView.setText(jsonError);
            }

            emptyListTextView.setVisibility(View.VISIBLE);
            mainProgressBar.setVisibility(View.GONE);

        } catch (Exception e) {
            //If there is an error, show a message to the user
            Toast.makeText(this, jsonData, Toast.LENGTH_SHORT).show();
            emptyListTextView.setVisibility(View.VISIBLE);
            mainProgressBar.setVisibility(View.GONE);
        }
    }

    private void fetchMoviesToList() {
        mainProgressBar.setVisibility(View.GONE);
        emptyListTextView.setVisibility(View.GONE);
        mainListView.setVisibility(View.VISIBLE);

        if (moviesArrayList.isEmpty()) {
            mainListView.setAdapter(null);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            MoviesAdapter mainListAdapter = new MoviesAdapter(MainActivity.this, moviesArrayList);
            mainListView.setAdapter(mainListAdapter);
        }
    }
}
