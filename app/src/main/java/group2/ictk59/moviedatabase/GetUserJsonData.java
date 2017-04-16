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
 * Created by ZinZin on 4/9/2017.
 */

public class GetUserJsonData extends GetRawData {

    private String LOG_TAG = GetUserJsonData.class.getSimpleName();
    private List<Object> mMovies ;
    private Uri mDestinationUri;

    public List<Object> getMovies() {
        return mMovies;
    }

    public Uri getDestinationUri() {
        return mDestinationUri;
    }

    public GetUserJsonData(String accessToken) {
        super(null);
        mMovies = new ArrayList<>();
        createAndUpdateUri(accessToken);
    }

    public void execute(){
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.i(LOG_TAG, mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String accessToken){
        final String URL_FEED = "http://localhost:5000/api/user";
        final String ACCESS_TOKEN = "access_token";

        mDestinationUri = Uri.parse(URL_FEED).buildUpon()
                .appendQueryParameter(ACCESS_TOKEN, accessToken)
                .build();

        return mDestinationUri != null;
    }

    public void processResult(){
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw data");
            return;
        }

        try {
            JSONArray jsonArray = new JSONObject(getmData()).getJSONArray(Constants.DATA)
                    .getJSONObject(0).getJSONObject(Constants.ATTRIBUTES)
                    .getJSONObject(Constants.WATCHLIST).getJSONArray(Constants.DATA);
            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonMovie = jsonArray.getJSONObject(i);
                Long id = jsonMovie.getLong(Constants.ID);
                JSONObject jsonMovieAtt = jsonMovie.getJSONObject(Constants.ATTRIBUTES);
                String poster = jsonMovieAtt.getString(Constants.POSTER);
                String title = jsonMovieAtt.getString(Constants.TITLE);
                String year = jsonMovieAtt.getString(Constants.YEAR);
                String casts = jsonMovieAtt.getString(Constants.CASTS);
                String rating = jsonMovieAtt.getString(Constants.RATING);

                Movie movie = new Movie(id, title, year, null, null, null, null,
                        null, null, casts, null, poster, rating, null, null, null);
                mMovies.add(movie);
            }
        } catch (JSONException e) {
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
