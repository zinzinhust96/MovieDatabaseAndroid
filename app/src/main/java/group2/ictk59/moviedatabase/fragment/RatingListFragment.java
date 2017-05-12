package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetUserRatingJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.activity.BaseActivity;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.RatedMovieListRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/28/2017.
 */

public class RatingListFragment extends Fragment implements RecyclerViewClickListener {
    private SearchView mSearchView;
    private MenuItem searchItem;

    private RecyclerView rvMovieList;
    private RatedMovieListRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView tvNoResults;
    OnItemSelectedListener mCallback;

    private List<Object> ratedMovies;

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

        mAdapter = new RatedMovieListRecyclerViewAdapter(getActivity(), new ArrayList<>(), this);
        rvMovieList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String accessToken = RESTServiceApplication.getInstance().getAccessToken();
        ProcessMovieList processMovieList = new ProcessMovieList(accessToken);
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Rating History");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_rated_movies, menu);

        searchItem = menu.findItem(R.id.menu_search);
        mSearchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) (getActivity()).getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo((getActivity()).getComponentName()));
//        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
                FragmentManager fm = (getActivity()).getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.IMDB_QUERY, query);
                SearchResultFragment searchResultFragment = new SearchResultFragment();
                searchResultFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.content_frame, searchResultFragment).addToBackStack(null).commit();
//                mSearchView.clearFocus();
                // anywhere you have a search item selected and are ready to close the search view...
                // clear the search query in the toolbar so it is empty the next time the user
                // opens the search view
                mSearchView.setQuery("", false);  // 2nd argument is false so it doesn't re-submit a blank search query
                // hide the SearchView action so the toolbar returns to normal mode showing the title and other menu items, etc.
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_top_rated:
                sortByRating();
                return true;
            case R.id.menu_most_recent:
                mAdapter.loadNewData(ratedMovies);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortByRating(){
        List<Object> topRated = new ArrayList<>(ratedMovies);
        Collections.sort(topRated, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                Float left = Float.parseFloat(((Movie)lhs).getRating());
                Float right = Float.parseFloat(((Movie)rhs).getRating());
                return right.compareTo(left);
            }
        });
        mAdapter.loadNewData(topRated);
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Movie)mAdapter.getListItem(position)).getId();
        mCallback.onMovieSelected(id);
    }

    @Override
    public void onViewClicked(View v, int position) {
        final Long id = ((Movie)mAdapter.getListItem(position)).getId();
        if (v.getId() == R.id.ivAdd){
            mCallback.onViewAddSelected(id);
        }else if (v.getId() == R.id.ivRemove){
            mCallback.onViewRemoveSelected(id);
        }
    }

    private class ProcessMovieList extends GetUserRatingJsonData {
        private ProcessMovieList(String accessToken) {
            super(accessToken);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        private class ProcessData extends DownloadJsonData {
            @Override
            protected void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                rvMovieList.setVisibility(View.GONE);
                BaseActivity.getRESTApplicationInfo(RESTServiceApplication.getInstance().getAccessToken(), getActivity());
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                ratedMovies = getMovies();
                if (ratedMovies.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    ratedMovies = getMovies();
                    Collections.reverse(ratedMovies);
                    mAdapter.loadNewData(ratedMovies);
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
