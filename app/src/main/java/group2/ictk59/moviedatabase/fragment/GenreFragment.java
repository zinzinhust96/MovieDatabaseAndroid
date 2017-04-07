package group2.ictk59.moviedatabase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/29/2017.
 */

public class GenreFragment extends Fragment {

    private ListView lvGenres;
    private String[] genreArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_genre, container, false);

        lvGenres = (ListView)rootView.findViewById(R.id.lvGenres);
        genreArray = getResources().getStringArray(R.array.genre_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, genreArray);
        lvGenres.setAdapter(arrayAdapter);

        lvGenres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toMovieListFragment(genreArray[position]);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Best " + genreArray[position] + " Movies");
            }
        });

        return rootView;
    }

    private void toMovieListFragment(String genre){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("genre", genre);
        bundle.putString("orderby", "rating");
        bundle.putBoolean("desc", true);
        MovieListFragment movieListFragment = new MovieListFragment();
        movieListFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, movieListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Best Movies by Genre");
    }
}
