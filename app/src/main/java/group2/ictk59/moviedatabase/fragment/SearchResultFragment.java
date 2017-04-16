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
import android.util.Log;
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
import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.LoginActivity;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.ComplexRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/13/2017.
 */

public class SearchResultFragment extends Fragment implements RecyclerViewClickListener {

    private static final String LOG_TAG = "SearchResultFragment";

    private RecyclerView rvSearchResult;
    private ComplexRecyclerViewAdapter mSearchResultViewAdapter;
    private TextView tvNoResults;
    private ProgressBar mProgressBar;
    private ArrayList<Object> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

        rvSearchResult = (RecyclerView)rootView.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mSearchResultViewAdapter = new ComplexRecyclerViewAdapter(getActivity(), new ArrayList<>(), this);
        rvSearchResult.setAdapter(mSearchResultViewAdapter);

        tvNoResults = (TextView)rootView.findViewById(R.id.tvNoResults);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        items = new ArrayList<>();
        String query = getArguments().getString(Constants.IMDB_QUERY);
        Log.e(Constants.IMDB_QUERY, query);
        if (query.length() > 0) {
            ProcessMovieList processMovieList = new ProcessMovieList(query);
            processMovieList.execute();
            ProcessActorList processActorList = new ProcessActorList(query);
            processActorList.execute();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search Result");
    }

    @Override
    public void onRowClicked(int position) {
        Object item = items.get(position);
        Handler handler = new Handler();
        if (item instanceof Movie) {
            Long id = ((Movie)item).getId();
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.ID, id);
            final MovieProfileFragment movieProfileFragment = new MovieProfileFragment();
            movieProfileFragment.setArguments(bundle);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ft.replace(R.id.content_frame, movieProfileFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }, 500);
        } else if (item instanceof Actor) {
            Long id = ((Actor)item).getId();
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.ID, id);
            final ActorProfileFragment actorProfileFragment = new ActorProfileFragment();
            actorProfileFragment.setArguments(bundle);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ft.replace(R.id.content_frame, actorProfileFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }, 500);
        }
    }

    @Override
    public void onViewClicked(View v, int position) {
        Object item = items.get(position);
        if (item instanceof Movie) {
            final Long id = ((Movie)item).getId();
            JsonObject object = new JsonObject();
            object.addProperty("action", "modify_watchlist");
            object.addProperty("movie_id", id.toString());
            if (v.getId() == R.id.ivAdd) {
                if (RESTServiceApplication.getInstance().isLogin()) {
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
                                        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
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
                } else {
                    Toast.makeText(getActivity(), R.string.login_alert, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

            } else if (v.getId() == R.id.ivRemove) {
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
                                    if (status.equalsIgnoreCase(Constants.SUCCESS)) {
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
    }

    public class ProcessMovieList extends GetMovieJsonData {
        public ProcessMovieList(String name) {
            super(name);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                items.addAll(getMovies());
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }

    public class ProcessActorList extends GetActorJsonData {
        public ProcessActorList(String name) {
            super(name);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                items.addAll(getActors());
                if (items.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    mSearchResultViewAdapter.loadNewData(items);
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
