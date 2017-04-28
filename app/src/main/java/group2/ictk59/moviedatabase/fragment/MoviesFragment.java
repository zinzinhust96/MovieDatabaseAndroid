package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class MoviesFragment extends BaseFragment implements RecyclerViewClickListener{

    private ListView lvMoviesItems;
    private RecyclerView rvMovieHorizontal;
    private AdapterHorizontal mAdapter;
    private ProgressBar mProgressBar;
    OnItemSelectedListener mCallback;

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
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

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
                switch (position){
                    case 0:
                        mCallback.toMovieListFragment("rating", true);
                        break;
                    case 1:
                        mCallback.toMovieListFragment("rating", false);
                        break;
                    case 2:
                        mCallback.toMovieListFragment("year", true);
                        break;
                    case 3:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
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


    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkConnected()){
            AlertDialogWrapper.showAlertDialog(getActivity());
        }else{
            ProcessMovieList processMovieList = new ProcessMovieList("", "year", true, "10");
            processMovieList.execute();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Movie");
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
                Log.d(Constants.TOKEN, getMovies().toString());
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
