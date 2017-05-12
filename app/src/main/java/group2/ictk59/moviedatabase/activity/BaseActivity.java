package group2.ictk59.moviedatabase.activity;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;

/**
 * Created by ZinZin on 3/25/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    protected Toolbar activateToolbar(){
        if (mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.app_bar);
            if (mToolbar != null){
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnable(){
        activateToolbar();
        if (mToolbar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }

    public static void getRESTApplicationInfo(String accessToken, Context context){
        Ion.with(context)
                .load("GET", Constants.BASE_URL + "/api/user?" + Constants.ACCESS_TOKEN + "=" + accessToken)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject jsonAttribute = new JSONObject(result).getJSONArray(Constants.DATA)
                                    .getJSONObject(0).getJSONObject(Constants.ATTRIBUTES);

                            JSONArray jsonWatchlist = jsonAttribute.getJSONObject(Constants.WATCHLIST)
                                    .getJSONArray(Constants.DATA);
                            List<Long> ids = new ArrayList<>();
                            for (int i = 0; i < jsonWatchlist.length(); i++){
                                JSONObject jsonMovie = jsonWatchlist.getJSONObject(i);
                                Long id = jsonMovie.getLong(Constants.ID);
                                Log.d(Constants.TOKEN, id.toString());
                                ids.add(id);
                            }
                            RESTServiceApplication.getInstance().setWatchlistId(ids);

                            JSONArray jsonRatedMovies = jsonAttribute.getJSONObject(Constants.RATED_MOVIES)
                                    .getJSONArray(Constants.DATA);
                            LongSparseArray<String> ratedMovies = new LongSparseArray<String>();
                            for (int i = 0; i < jsonRatedMovies.length(); i++){
                                JSONObject jsonRatedMoviesAtt = jsonRatedMovies.getJSONObject(i)
                                        .getJSONObject(Constants.ATTRIBUTES);
                                Long movieId = jsonRatedMoviesAtt.getLong(Constants.MOVIE_ID);
                                String rating = jsonRatedMoviesAtt.getString(Constants.RATING);
                                ratedMovies.put(movieId, rating);
                            }
                            RESTServiceApplication.getInstance().setRatedMovies(ratedMovies);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
