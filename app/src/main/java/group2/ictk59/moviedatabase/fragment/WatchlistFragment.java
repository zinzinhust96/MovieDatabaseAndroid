package group2.ictk59.moviedatabase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetUserJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.LoginActivity;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.ComplexRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/7/2017.
 */

public class WatchlistFragment extends Fragment implements RecyclerViewClickListener {

    private RecyclerView rvMovieList;
    private ComplexRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView tvNoResults;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watchlist, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        rvMovieList = (RecyclerView)rootView.findViewById(R.id.rvMovieList);
        tvNoResults = (TextView)rootView.findViewById(R.id.tvNoResults);
        rvMovieList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mAdapter = new ComplexRecyclerViewAdapter(getActivity(), new ArrayList<>(), this);
        rvMovieList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String accessToken = RESTServiceApplication.getInstance().getAccessToken();
        ProcessMovieList processMovieList = new ProcessMovieList(accessToken);
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Your Watchlist");
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Movie)mAdapter.getListItem(position)).getId();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, id);
        final MovieProfileFragment movieProfileFragment = new MovieProfileFragment();
        movieProfileFragment.setArguments(bundle);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ft.replace(R.id.content_frame, movieProfileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }, 500);
    }

    @Override
    public void onViewClicked(View v, int position) {
        final Long id = ((Movie)mAdapter.getListItem(position)).getId();
        JsonObject object = new JsonObject();
        object.addProperty("action", "modify_watchlist");
        object.addProperty("movie_id", id.toString());
        if (v.getId() == R.id.ivAdd){
            if (RESTServiceApplication.getInstance().isLogin()){
                Ion.with(getActivity())
                        .load("http://localhost:5000/api/user/action?" + Constants.ACCESS_TOKEN + "=" + RESTServiceApplication.getInstance().getAccessToken())
                        .setJsonObjectBody(object)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String status = jsonObject.getString(Constants.STATUS);
                                    if (status.equalsIgnoreCase(Constants.SUCCESS)){
                                        //add to list<long> watchlistId
                                        List<Long> watchlistId = RESTServiceApplication.getInstance().getWatchlistId();
                                        watchlistId.add(id);
                                        RESTServiceApplication.getInstance().setWatchlistId(watchlistId);
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            }else {
                Toast.makeText(getActivity(), R.string.login_alert, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

        }else if (v.getId() == R.id.ivRemove){
            Ion.with(getActivity())
                    .load("http://localhost:5000/api/user/action?" + Constants.ACCESS_TOKEN + "=" + RESTServiceApplication.getInstance().getAccessToken())
                    .setJsonObjectBody(object)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.getString(Constants.STATUS);
                                if (status.equalsIgnoreCase(Constants.SUCCESS)){
                                    //add to list<long> watchlistId
                                    List<Long> watchlistId = RESTServiceApplication.getInstance().getWatchlistId();
                                    watchlistId.remove(id);
                                    RESTServiceApplication.getInstance().setWatchlistId(watchlistId);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

    public class ProcessMovieList extends GetUserJsonData {
        public ProcessMovieList(String accessToken) {
            super(accessToken);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            @Override
            protected void onPreExecute() {
//                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                List movies = getMovies();
                if (movies.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    mAdapter.loadNewData(getMovies());
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
