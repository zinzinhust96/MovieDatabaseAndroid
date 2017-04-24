package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import group2.ictk59.moviedatabase.GetUserJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.SearchActivity;
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
    OnItemSelectedListener mCallback;

    private List<Object> watchlist;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(RESTServiceApplication.getInstance().getUsername() + "'s Watchlist");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_watchlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_sort_rating:
                sortByRating();
                return true;
            case R.id.menu_sort_year:
                sortByYear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortByRating(){
        Collections.sort(watchlist, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((Movie)rhs).getRating().compareTo(((Movie)lhs).getRating());
            }
        });
        mAdapter.loadNewData(watchlist);
    }

    private void sortByYear(){
        Collections.sort(watchlist, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((Movie)rhs).getYear().compareTo(((Movie)lhs).getYear());
            }
        });
        mAdapter.loadNewData(watchlist);
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Movie)mAdapter.getListItem(position)).getId();
        mCallback.onMovieSelected(id);
    }

    @Override
    public void onViewClicked(View v, int position) {
        final Long id = ((Movie)mAdapter.getListItem(position)).getId();
        mCallback.onViewSelected(v, id);
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
                mProgressBar.setVisibility(View.VISIBLE);
                rvMovieList.setVisibility(View.GONE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                watchlist = getMovies();
                if (watchlist.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    mAdapter.loadNewData(getMovies());
                    rvMovieList.setVisibility(View.VISIBLE);
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
