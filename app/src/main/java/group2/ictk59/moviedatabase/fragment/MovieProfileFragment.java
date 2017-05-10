package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.LoginActivity;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/10/2017.
 */

public class MovieProfileFragment extends Fragment implements RecyclerViewClickListener {

    AdapterHorizontal mAdapter;

    ExpandableTextView etvPlot;
    TextView tvTitleYear, tvGenre, tvRuntime, tvRating, tvVotes, tvAwards, tvCountry, tvReleased, tvDirector, tvWriter, tvYourRating;
    ImageView ivPoster;
    Button btAdd, btRemove;
    RecyclerView rvActorList;
    RatingBar ratingBar;
    ProgressDialog progressDialog;

    private List topCasts;
    OnItemSelectedListener mCallback;
    private Long id;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;

        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movieprofile, container, false);
        setUpView(rootView);

        return rootView;
    }

    private void setUpView(View view) {
        etvPlot = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
        tvTitleYear = (TextView) view.findViewById(R.id.tvTitleYear);
        tvGenre = (TextView) view.findViewById(R.id.tvGenre);
        tvRuntime = (TextView) view.findViewById(R.id.tvRuntime);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        tvVotes = (TextView) view.findViewById(R.id.tvVotes);
        tvAwards = (TextView) view.findViewById(R.id.tvAwards);
        tvCountry = (TextView) view.findViewById(R.id.tvCountry);
        tvReleased = (TextView) view.findViewById(R.id.tvReleased);
        tvDirector = (TextView) view.findViewById(R.id.tvDirector);
        tvWriter = (TextView) view.findViewById(R.id.tvWriter);
        ivPoster = (ImageView) view.findViewById(R.id.ivPoster);
        btAdd = (Button) view.findViewById(R.id.btAdd);
        btRemove = (Button) view.findViewById(R.id.btRemove);
        rvActorList = (RecyclerView) view.findViewById(R.id.rvActorList);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvActorList.setLayoutManager(layoutManager);
        mAdapter = new AdapterHorizontal(getActivity(), new ArrayList<>(), this);
        rvActorList.setAdapter(mAdapter);

        id = getArguments().getLong(Constants.ID);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddButton(false);
                Toast.makeText(getActivity(), "Added to watchlist!", Toast.LENGTH_SHORT).show();
                mCallback.onViewAddSelected(id);
            }
        });

        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddButton(true);
                mCallback.onViewRemoveSelected(id);
            }
        });

        tvYourRating = (TextView) view.findViewById(R.id.tvYourRating);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(false);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                if (fromUser) {
                    if (RESTServiceApplication.getInstance().isLogin()) {
                        tvYourRating.setText("Your rating: " + rating + "/10");

                        LongSparseArray<String> ratedMovies = RESTServiceApplication.getInstance().getRatedMovies();
                        ratedMovies.put(id, String.valueOf(rating));
                        RESTServiceApplication.getInstance().setRatedMovies(ratedMovies);

                        String accessToken = RESTServiceApplication.getInstance().getAccessToken();
                        final JsonObject json = new JsonObject();
                        json.addProperty(Constants.ACTION, Constants.RATE_MOVIE);
                        json.addProperty(Constants.MOVIE_ID, id.toString());
                        json.addProperty(Constants.RATING, String.valueOf(rating));
                        Ion.with(getActivity())
                                .load(Constants.BASE_URL + "/api/user/action?" + Constants.ACCESS_TOKEN + "=" + accessToken)
                                .setJsonObjectBody(json)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            String status = jsonObject.getString(Constants.STATUS);
                                            if (status.equalsIgnoreCase(Constants.SUCCESS)) {
                                                tvRating.setText(jsonObject.getString(Constants.RATING) + "/10");
                                                tvVotes.setText("(" + jsonObject.getString(Constants.VOTES) + " votes)");
                                            }
                                            Toast.makeText(getActivity(), jsonObject.getString(Constants.MESSAGE), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        } catch (NullPointerException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(getActivity(), R.string.login_alert, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }
            }
        });
    }

    private void updateView(final Movie movie) {
        etvPlot.setText(movie.getPlot());
        tvTitleYear.setText(movie.getTitle() + " (" + movie.getYear() + ")");
        tvGenre.setText(movie.getGenre());
        tvRuntime.setText(movie.getRuntime());
        tvVotes.setText("");
        tvAwards.setText(movie.getAwards());
        tvCountry.setText(movie.getCountry());
        tvReleased.setText(movie.getReleased());
        tvDirector.setText(movie.getDirector());
        tvRating.setText(movie.getRating() + "/10");
        tvVotes.setText("(" + movie.getVotes() + " votes)");
        tvWriter.setText(movie.getWriter());
        Picasso.with(getActivity())
                .load(movie.getPoster())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(ivPoster);
        topCasts = (List) movie.getTopCasts();
        mAdapter.loadNewData(topCasts);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RESTServiceApplication.getInstance().isLogin()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(500);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Long id = getArguments().getLong(Constants.ID);
                            List<Long> watchlistId = RESTServiceApplication.getInstance().getWatchlistId();
                            if (watchlistId != null) {
                                if (watchlistId.contains(id)) {
                                    showAddButton(false);
                                }
                            }
                            //Rating bar update view
                            LongSparseArray<String> ratedMovies = RESTServiceApplication.getInstance().getRatedMovies();
                            if (ratedMovies != null) {
                                if (ratedMovies.get(id) != null) {
                                    String rating = ratedMovies.get(id);
                                    tvYourRating.setText("Your rating: " + rating + "/10");
                                    ratingBar.setRating(Float.parseFloat(rating));
                                }
                            }
                        }
                    });
                }
            }).start();
        }else{
            showAddButton(true);
            ratingBar.setRating(0);
        }
        ProcessMovieList processMovieList = new ProcessMovieList(id);
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Movie Profile");
    }

    private void showAddButton(boolean isShow) {
        btAdd.setVisibility(isShow ? View.VISIBLE : View.GONE);
        btRemove.setVisibility(isShow ? View.GONE : View.VISIBLE);
        btAdd.setClickable(isShow);
        btRemove.setClickable(!isShow);
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Actor) topCasts.get(position)).getId();
        mCallback.onActorSelected(id);
    }

    @Override
    public void onViewClicked(View v, int position) {

    }

    private class ProcessMovieList extends GetMovieJsonData {
        private ProcessMovieList(Long id) {
            super(id);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        private class ProcessData extends DownloadJsonData {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getActivity(), "", "Retrieving latest data...", true);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                List movies = getMovies();
                Movie movie = (Movie) movies.get(0);
                updateView(movie);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
