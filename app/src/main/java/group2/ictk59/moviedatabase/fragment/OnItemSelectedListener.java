package group2.ictk59.moviedatabase.fragment;

/**
 * Created by ZinZin on 4/19/2017.
 */

public interface OnItemSelectedListener {
    void onMovieSelected(Long id);
    void onActorSelected(Long id);
    void toMovieListFragment(String orderBy, boolean desc);
    void toActorListFragment(String orderBy, boolean desc);
    void onViewAddSelected(Long id);
    void onViewRemoveSelected(Long id);
}
