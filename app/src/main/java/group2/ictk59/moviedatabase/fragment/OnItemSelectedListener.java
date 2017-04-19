package group2.ictk59.moviedatabase.fragment;

import android.view.View;

/**
 * Created by ZinZin on 4/19/2017.
 */

public interface OnItemSelectedListener {
    public void onMovieSelected(Long id);
    public void onActorSelected(Long id);
    public void onViewSelected(View v, Long id);
    public void toMovieListFragment(String orderBy, boolean desc);
    public void toActorListFragment(String orderBy, boolean desc);
}
