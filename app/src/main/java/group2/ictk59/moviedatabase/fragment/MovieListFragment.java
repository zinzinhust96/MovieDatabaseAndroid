package group2.ictk59.moviedatabase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.recycleview.ListRecyclerViewAdapter;


/**
 * Created by ZinZin on 3/31/2017.
 */

public class MovieListFragment extends Fragment {

    private RecyclerView rvMovieList;
    private ListRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        rvMovieList = (RecyclerView)rootView.findViewById(R.id.rvMovieList);
        rvMovieList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mAdapter = new ListRecyclerViewAdapter(getActivity().getApplicationContext(), new ArrayList<>());
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
        ProcessMovieList processMovieList = new ProcessMovieList(genre, orderBy, desc, "50");
        processMovieList.execute();
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
//                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mAdapter.loadNewData(getMovies());
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
