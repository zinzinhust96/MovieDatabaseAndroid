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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class CelebsFragment extends Fragment {

    private ListView lvActorsItems;
    private RecyclerView rvActorHorizontal;
    private AdapterHorizontal mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_celebs, container, false);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvActorHorizontal = (RecyclerView)rootView.findViewById(R.id.rvActorHorizontal);
        rvActorHorizontal.setLayoutManager(layoutManager);
        mAdapter = new AdapterHorizontal(getActivity().getApplicationContext(), new ArrayList<>());
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
                        toActorListFragment("popularity", true, ft);
                        break;
                    case 1:
                        break;
                }
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(actorsItems.get(position));
            }
        });

        return rootView;
    }

    private void toActorListFragment(String orderBy, boolean desc, FragmentTransaction ft){
        Bundle bundle = new Bundle();
        bundle.putString("orderby", orderBy);
        bundle.putBoolean("desc", desc);
        ActorListFragment actorListFragment = new ActorListFragment();
        actorListFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, actorListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ProcessActorList processMovieList = new ProcessActorList("popularity", true, "10");
        processMovieList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Celebs");
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
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
