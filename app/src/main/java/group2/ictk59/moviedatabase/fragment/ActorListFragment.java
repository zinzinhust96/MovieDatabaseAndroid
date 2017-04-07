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

import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.recycleview.ListRecyclerViewAdapter;

/**
 * Created by ZinZin on 4/1/2017.
 */

public class ActorListFragment extends Fragment {

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

        mAdapter = new ListRecyclerViewAdapter(getActivity().getApplicationContext(), new ArrayList<>());
        rvActorList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String orderBy = getArguments().getString("orderby");
        Boolean desc = getArguments().getBoolean("desc");
        ProcessActorList processMovieList = new ProcessActorList(orderBy, desc, "50");
        processMovieList.execute();
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
