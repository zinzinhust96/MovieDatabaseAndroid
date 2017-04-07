package group2.ictk59.moviedatabase.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/31/2017.
 */

public class MovieViewHolderHorizontal extends RecyclerView.ViewHolder {
    protected ImageView ivPoster;
    protected TextView tvTitle;

    public MovieViewHolderHorizontal(View view) {
        super(view);
        ivPoster = (ImageView)view.findViewById(R.id.ivPoster);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
    }
}
