package group2.ictk59.moviedatabase.fragment;

import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.ListRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerItemClickListener;


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
        rvMovieList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), rvMovieList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Long id = ((Movie)mAdapter.getListItem(position)).getId();
                Toast.makeText(getActivity().getApplicationContext(), id.toString(), Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.ID, id);
                MovieProfileFragment movieProfileFragment = new MovieProfileFragment();
                movieProfileFragment.setArguments(bundle);
                ft.replace(R.id.content_frame, movieProfileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

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
