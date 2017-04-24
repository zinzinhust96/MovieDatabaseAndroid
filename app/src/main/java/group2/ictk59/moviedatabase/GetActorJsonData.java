package group2.ictk59.moviedatabase;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.model.Movie;

/**
 * Created by ZinZin on 3/29/2017.
 */

public class GetActorJsonData extends GetRawData {

    private String LOG_TAG = GetActorJsonData.class.getSimpleName();
    private List<Object> mActors;
    private Uri mDestinationUri;

    public List<Object> getActors() {
        return mActors;
    }

    public Uri getDestinationUri() {
        return mDestinationUri;
    }

    public GetActorJsonData(Long id) {
        super(null);
        mActors = new ArrayList<>();
        createAndUpdateUri(id);
    }

    public GetActorJsonData(String name) {
        super(null);
        mActors = new ArrayList<>();
        createAndUpdateUri(name);
    }

    public GetActorJsonData(String orderBy, boolean desc, String limit){
        super(null);
        mActors = new ArrayList<>();
        createAndUpdateUri(orderBy, desc, limit);
    }

    public void execute(){
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.i(LOG_TAG, mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(Long id){
        final String URL_FEED = Constants.BASE_URL + "/api/actors/" + id.toString();
        mDestinationUri = Uri.parse(URL_FEED);
        return mDestinationUri != null;
    }

    public boolean createAndUpdateUri(String name){
        final String URL_FEED = Constants.BASE_URL + "/api/actors/search";
        final String NAME_PARAMS = "string";

        mDestinationUri = Uri.parse(URL_FEED).buildUpon()
                .appendQueryParameter(NAME_PARAMS, name)
                .build();

        return mDestinationUri != null;
    }

    public boolean createAndUpdateUri(String orderBy, boolean desc, String limit){
        final String URL_FEED = Constants.BASE_URL + "/api/actors";
        final String ORDERBY_PARAMS = "orderby";
        final String DESC_PARAMS = "desc";
        final String LIMIT_PARAMS = "limit";

        mDestinationUri = Uri.parse(URL_FEED).buildUpon()
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

        final String ACTOR_DATA = "data";
        final String ACTOR_ID = "id";
        final String ACTOR_ATTRIBUTES = "attributes";
        final String ACTOR_BIO = "biography";
        final String ACTOR_BIRTHDAY = "birthday";
        final String ACTOR_DEATHDAY = "deathday";
        final String ACTOR_NAME = "name";
        final String ACTOR_PLACEOFBIRTH = "place_of_birth";
        final String ACTOR_POPULARITY = "popularity";
        final String ACTOR_PROFILEPIC = "profile_pic";
        final String ACTOR_KNOWNFOR = "known_for";

        try {
            JSONObject jsonData = new JSONObject(getmData());

            JSONArray jsonActors = jsonData.getJSONArray(ACTOR_DATA);
//            JSONObject jsonData = new JSONObject(getmData());
//            JSONObject jsonEmbedded = jsonData.getJSONObject(EMBEDDED);
//            JSONArray jsonActors = jsonEmbedded.getJSONArray(MOVIE);
            for (int i = 0; i < jsonActors.length(); i++){
                JSONObject jsonActor = jsonActors.getJSONObject(i);
                Long id = jsonActor.getLong(ACTOR_ID);
                JSONObject jsonAttribute = jsonActor.getJSONObject(ACTOR_ATTRIBUTES);
                String biography = jsonAttribute.getString(ACTOR_BIO);
                String birthday = jsonAttribute.getString(ACTOR_BIRTHDAY);
                String deathday = jsonAttribute.getString(ACTOR_DEATHDAY);
                String name = jsonAttribute.getString(ACTOR_NAME);
                String placeOfBirth = jsonAttribute.getString(ACTOR_PLACEOFBIRTH);
                Double popularity = jsonAttribute.getDouble(ACTOR_POPULARITY);
                String profilePic = jsonAttribute.getString(ACTOR_PROFILEPIC);

                JSONArray jsonKnownFor = jsonAttribute.getJSONObject(ACTOR_KNOWNFOR).getJSONArray(ACTOR_DATA);
                List<Movie> knownFor = new ArrayList<>();
                for (int j = 0; j < jsonKnownFor.length(); j++){
                    JSONObject jsonMovie = jsonKnownFor.getJSONObject(j);
                    Long movieId = jsonMovie.getLong("id");
                    JSONObject jsonMovieAttribute = jsonMovie.getJSONObject(ACTOR_ATTRIBUTES);
                    String poster = jsonMovieAttribute.getString("poster");
                    String title = jsonMovieAttribute.getString("title");
                    String year = jsonMovieAttribute.getString("year");

                    Movie movie = new Movie(movieId, title, year, null, null, null, null, null, null, null, null, poster, null, null, null, null);
                    knownFor.add(movie);
                }

                Actor actorObject = new Actor(id, biography, birthday, deathday, name, placeOfBirth, popularity, profilePic, knownFor);
                mActors.add(actorObject);
            }

            for (Object actorObject: mActors){
                Log.v(LOG_TAG, actorObject.toString());
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
