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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.LoginActivity;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class MoviesFragment extends Fragment implements RecyclerViewClickListener{

    private ListView lvMoviesItems;
    private RecyclerView rvMovieHorizontal;
    private AdapterHorizontal mAdapter;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

//        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.app_bar);
//
//        final DrawerLayout drawer = (DrawerLayout)rootView.findViewById(R.id.drawer_layout);
//        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMovieHorizontal = (RecyclerView)rootView.findViewById(R.id.rvMovieHorizontal);
        rvMovieHorizontal.setLayoutManager(layoutManager);
        mAdapter = new AdapterHorizontal(getActivity(), new ArrayList<>(), this);
        rvMovieHorizontal.setAdapter(mAdapter);

        lvMoviesItems = (ListView)rootView.findViewById(R.id.lvMoviesItems);
        final List<String> moviesItems = new ArrayList<>();
        moviesItems.add("Top Rated Movies");
        moviesItems.add("Lowest Rated Movies");
        moviesItems.add("Latest Featured Movies");
        moviesItems.add("Best Movies by Genre");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_selectable_list_item, moviesItems);
        lvMoviesItems.setDivider(null);
        lvMoviesItems.setAdapter(arrayAdapter);

        lvMoviesItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                switch (position){
                    case 0:
                        toMovieListFragment("rating", true, ft);
                        break;
                    case 1:
                        toMovieListFragment("rating", false, ft);
                        break;
                    case 2:
                        toMovieListFragment("year", true, ft);
                        break;
                    case 3:
                        ft.replace(R.id.content_frame, new GenreFragment());
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                }

            }
        });

        return rootView;
    }

    private void showProgressBar(boolean isShow){
        mProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        rvMovieHorizontal.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
    }

    private void toMovieListFragment(String orderBy, boolean desc, FragmentTransaction ft){
        Bundle bundle = new Bundle();
        bundle.putString("orderby", orderBy);
        bundle.putBoolean("desc", desc);
        MovieListFragment movieListFragment = new MovieListFragment();
        movieListFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, movieListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ProcessMovieList processMovieList = new ProcessMovieList("", "year", true, "10");
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Movie");
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

    public class ProcessMovieList extends GetMovieJsonData {
        public ProcessMovieList(String genre, String orderBy, boolean desc, String limit) {
            super(genre, orderBy, desc, limit);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends GetMovieJsonData.DownloadJsonData {
            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mAdapter.loadNewData(getMovies());
                showProgressBar(false);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }


}
