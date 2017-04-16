package group2.ictk59.moviedatabase.recycleview;

import android.view.View;

/**
 * Created by ZinZin on 4/14/2017.
 */

public interface RecyclerViewClickListener {

    void onRowClicked(int position);
    void onViewClicked(View v, int position);
}
