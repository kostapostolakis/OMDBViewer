package com.test.omdbviewer;

class Movie {

    static final String SEARCH = "Search";

    static final String TITLE = "Title";
    static final String YEAR = "Year";
    static final String IMDB_ID = "imdbID";
    static final String POSTER = "Poster";
    static final String RELEASED = "Released";
    static final String RATED = "Rated";
    static final String RUNTIME = "Runtime";
    static final String GENRE = "Genre";
    static final String DIRECTOR = "Director";
    static final String WRITER = "Writer";
    static final String ACTORS = "Actors";
    static final String PLOT = "Plot";

    static final String RATINGS = "Ratings";
    static final String SOURCE = "Source";
    static final String VALUE = "Value";

    static final String RATING_IMDB = "Internet Movie Database";
    static final String RATING_ROTTEN = "Rotten Tomatoes";
    static final String RATING_METACRITIC = "Metacritic";

    static final String ERROR = "Error";
    static final String MOVIE_NOT_FOUND = "Movie not found!";

    private String imdbID, title, year, poster;

    Movie(String imdbID, String title, String year, String poster) {
        this.imdbID = imdbID;
        this.title = title;
        this.year = year;
        this.poster = poster;
    }

    String getImdbID() {
        return imdbID;
    }

    String getTitle() {
        return title;
    }

    String getYear() {
        return year;
    }

    String getPoster() {
        return poster;
    }
}
