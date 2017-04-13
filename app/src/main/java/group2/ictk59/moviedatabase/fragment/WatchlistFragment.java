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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetUserJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.ComplexRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerItemClickListener;

/**
 * Created by ZinZin on 4/7/2017.
 */

public class WatchlistFragment extends Fragment {

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

        mAdapter = new ComplexRecyclerViewAdapter(getActivity().getApplicationContext(), new ArrayList<>());
        rvMovieList.setAdapter(mAdapter);
        rvMovieList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), rvMovieList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Long id = ((Movie)mAdapter.getListItem(position)).getId();
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
        String accessToken = RESTServiceApplication.getInstance().getAccessToken();
        ProcessMovieList processMovieList = new ProcessMovieList(accessToken);
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Your Watchlist");
    }

    public class ProcessMovieList extends GetUserJsonData {
        public ProcessMovieList(String accessToken) {
            super(accessToken);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends GetUserJsonData.DownloadJsonData {
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
