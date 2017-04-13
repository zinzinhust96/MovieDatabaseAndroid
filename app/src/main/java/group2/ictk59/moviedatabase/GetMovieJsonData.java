package group2.ictk59.moviedatabase;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.model.Movie;

/**
 * Created by ZinZin on 3/24/2017.
 */

public class GetMovieJsonData extends GetRawData {

    private String LOG_TAG = GetMovieJsonData.class.getSimpleName();
    private List<Object> mMovies ;
    private Uri mDestinationUri;

    public List<Object> getMovies() {
        return mMovies;
    }

    public Uri getDestinationUri() {
        return mDestinationUri;
    }

    public GetMovieJsonData(Long id) {
        super(null);
        mMovies = new ArrayList<>();
        createAndUpdateUri(id);
    }

    public GetMovieJsonData(String name) {
        super(null);
        mMovies = new ArrayList<>();
        createAndUpdateUri(name);
    }

    public GetMovieJsonData(String genre, String orderBy, Boolean desc, String limit){
        super(null);
        mMovies = new ArrayList<>();
        createAndUpdateUri(genre, orderBy, desc, limit);
    }

    public void execute(){
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.i(LOG_TAG, mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(Long id){
        final String URL_FEED = "http://localhost:5000/api/movies/" + id.toString();
        mDestinationUri = Uri.parse(URL_FEED);
        return mDestinationUri != null;
    }

    public boolean createAndUpdateUri(String name){
        final String URL_FEED = "http://localhost:5000/api/movies/search";
        final String NAME_PARAMS = "string";

        mDestinationUri = Uri.parse(URL_FEED).buildUpon()
                .appendQueryParameter(NAME_PARAMS, name)
                .build();

        return mDestinationUri != null;
    }

    public boolean createAndUpdateUri(String genre, String orderBy, boolean desc, String limit){
        final String URL_FEED = "http://localhost:5000/api/movies";
        final String GENRE_PARAMS = "genre";
        final String ORDERBY_PARAMS = "orderby";
        final String DESC_PARAMS = "desc";
        final String LIMIT_PARAMS = "limit";

        mDestinationUri = Uri.parse(URL_FEED).buildUpon()
                .appendQueryParameter(GENRE_PARAMS, genre)
                .appendQueryParameter(ORDERBY_PARAMS, orderBy)
                .appendQueryParameter(DESC_PARAMS, desc ? "true" : "false")
                .appendQueryParameter(LIMIT_PARAMS, limit)
                .build();

        return mDestinationUri != null;
    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw data");
            return;
        }

        final String MOVIE_DATA = "data";
        final String MOVIE_ID = "id";
        final String MOVIE_ATTRIBUTES = "attributes";
        final String MOVIE_TITLE = "title";
        final String MOVIE_YEAR = "year";
        final String MOVIE_RELEASED = "released";
        final String MOVIE_RUNTIME = "runtime";
        final String MOVIE_COUNTRY = "country";
        final String MOVIE_GENRE = "genre";
        final String MOVIE_DIRECTOR = "director";
        final String MOVIE_AWARDS = "awards";
        final String MOVIE_CASTS = "casts";
        final String MOVIE_PLOT = "plot";
        final String MOVIE_POSTER = "poster";
        final String MOVIE_RATING = "rating";
        final String MOVIE_VOTES = "votes";
        final String MOVIE_WRITER = "writer";
        final String MOVIE_ACTORS = "actors";

        try {
            JSONObject jsonData = new JSONObject(getmData());

            JSONArray jsonMovies = jsonData.getJSONArray(MOVIE_DATA);
//            JSONObject jsonData = new JSONObject(getmData());
//            JSONObject jsonEmbedded = jsonData.getJSONObject(EMBEDDED);
//            JSONArray jsonMovies = jsonEmbedded.getJSONArray(MOVIE);
            for (int i = 0; i < jsonMovies.length(); i++){
                JSONObject jsonMovie = jsonMovies.getJSONObject(i);
                Long id = jsonMovie.getLong(MOVIE_ID);
                JSONObject jsonAttribute = jsonMovie.getJSONObject(MOVIE_ATTRIBUTES);
                String title = jsonAttribute.getString(MOVIE_TITLE);
                String year = jsonAttribute.getString(MOVIE_YEAR);
                String released = jsonAttribute.getString(MOVIE_RELEASED);
                String runtime = jsonAttribute.getString(MOVIE_RUNTIME);
                String country = jsonAttribute.getString(MOVIE_COUNTRY);
                String genre = jsonAttribute.getString(MOVIE_GENRE);
                String director = jsonAttribute.getString(MOVIE_DIRECTOR);
                String awards = jsonAttribute.getString(MOVIE_AWARDS);
                String casts = jsonAttribute.getString(MOVIE_CASTS);
                String plot = jsonAttribute.getString(MOVIE_PLOT);
                String poster = jsonAttribute.getString(MOVIE_POSTER);
                String rating = jsonAttribute.getString(MOVIE_RATING);
                String votes = jsonAttribute.getString(MOVIE_VOTES);
                String writer = jsonAttribute.getString(MOVIE_WRITER);

                Movie movieObject = new Movie(id, title, year, released, runtime, country, genre, director, awards, casts, plot, poster, rating, votes, writer, null);
                mMovies.add(movieObject);
            }

            for (Object movieObject: mMovies){
                Log.v(LOG_TAG, movieObject.toString());
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public class DownloadJsonData extends DownloadRawData{
        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        @Override
        protected String doInBackground(String... strings) {
            String[] par = { mDestinationUri.toString() };
            return super.doInBackground(par);
        }
    }
}
