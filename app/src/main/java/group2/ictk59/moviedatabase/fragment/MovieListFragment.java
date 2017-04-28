package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.ListRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;


/**
 * Created by ZinZin on 3/31/2017.
 */

public class MovieListFragment extends BaseFragment implements RecyclerViewClickListener {

    private RecyclerView rvMovieList;
    private ListRecyclerViewAdapter mAdapter;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        rvMovieList = (RecyclerView)rootView.findViewById(R.id.rvMovieList);
        rvMovieList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mAdapter = new ListRecyclerViewAdapter(getActivity(), new ArrayList<>(), this);
        rvMovieList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String orderBy = getArguments().getString("orderby");
        Boolean desc = getArguments().getBoolean("desc");
        String genre = getArguments().getString("genre");
        if (genre == null){
            genre = "";
        }
        if (!isNetworkConnected()){
            AlertDialogWrapper.showAlertDialog(getActivity());
        }else {
            ProcessMovieList processMovieList = new ProcessMovieList(genre, orderBy, desc, "50");
            processMovieList.execute();
        }
        if (!genre.equalsIgnoreCase("")){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Best " + genre + " Movies");
        }
        else if (orderBy.equalsIgnoreCase("rating")){
            if (desc){
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Top Rated Movies");
            }else{
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Lowest Rated Movies");
            }
        }else if (orderBy.equalsIgnoreCase("year")){
            if (desc){
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Latest Featured Movies");
            }
        }
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
                mProgressBar.setVisibility(View.VISIBLE);
                rvMovieList.setVisibility(View.GONE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mAdapter.loadNewData(getMovies());
                mProgressBar.setVisibility(View.GONE);
                rvMovieList.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
