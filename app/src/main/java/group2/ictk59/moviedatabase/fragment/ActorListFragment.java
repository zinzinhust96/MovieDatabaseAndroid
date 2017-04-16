package group2.ictk59.moviedatabase.fragment;

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
import android.widget.Toast;

import java.util.ArrayList;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.recycleview.ListRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/1/2017.
 */

public class ActorListFragment extends Fragment implements RecyclerViewClickListener {

    private RecyclerView rvActorList;
    private ListRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actor_list, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        rvActorList = (RecyclerView)rootView.findViewById(R.id.rvActorList);
        rvActorList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mAdapter = new ListRecyclerViewAdapter(getActivity(), new ArrayList<>(), this);
        rvActorList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String orderBy = getArguments().getString("orderby");
        Boolean desc = getArguments().getBoolean("desc");
        if (orderBy != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Most Popular Celebrities");
        }
        ProcessActorList processMovieList = new ProcessActorList(orderBy, desc, "50");
        processMovieList.execute();
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Actor)mAdapter.getListItem(position)).getId();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, id);
        final ActorProfileFragment actorProfileFragment = new ActorProfileFragment();
        actorProfileFragment.setArguments(bundle);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ft.replace(R.id.content_frame, actorProfileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }, 500);

    }

    @Override
    public void onViewClicked(View v, int position) {

    }

    public class ProcessActorList extends GetActorJsonData {
        public ProcessActorList(String orderBy, boolean desc, String limit) {
            super(orderBy, desc, limit);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends GetActorJsonData.DownloadJsonData {
            @Override
            protected void onPreExecute() {
//                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                mAdapter.loadNewData(getActors());
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
