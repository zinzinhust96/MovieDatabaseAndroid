package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class CelebsFragment extends BaseFragment implements RecyclerViewClickListener {

    private ListView lvActorsItems;
    private RecyclerView rvActorHorizontal;
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
        View rootView = inflater.inflate(R.layout.fragment_celebs, container, false);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvActorHorizontal = (RecyclerView)rootView.findViewById(R.id.rvActorHorizontal);
        rvActorHorizontal.setLayoutManager(layoutManager);
        mAdapter = new AdapterHorizontal(getActivity(), new ArrayList<>(), this);
        rvActorHorizontal.setAdapter(mAdapter);

        lvActorsItems = (ListView)rootView.findViewById(R.id.lvActorsItem);
        final List<String> actorsItems = new ArrayList<>();
        actorsItems.add("Most Popular Celebrities");
        actorsItems.add("Born Today");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_selectable_list_item, actorsItems);
        lvActorsItems.setDivider(null);
        lvActorsItems.setAdapter(arrayAdapter);

        lvActorsItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                switch (position){
                    case 0:
                        mCallback.toActorListFragment("popularity", true);
                        break;
                    case 1:
                        ft.replace(R.id.content_frame, new BornTodayFragment()).addToBackStack(null).commit();
                        break;
                }
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(actorsItems.get(position));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkConnected()){
            AlertDialogWrapper.showAlertDialog(getActivity());
        }else {
            ProcessActorList processMovieList = new ProcessActorList("popularity", true, "10");
            processMovieList.execute();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Celebrities");
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Actor)mAdapter.getListItem(position)).getId();
        mCallback.onActorSelected(id);
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
                mProgressBar.setVisibility(View.VISIBLE);
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
