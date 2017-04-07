package group2.ictk59.moviedatabase.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/25/2017.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder {

    protected ImageView ivPoster;
    protected TextView tvTitleYear;
    protected TextView tvCasts;
    protected TextView tvRating;


    public MovieViewHolder(View view) {
        super(view);
        ivPoster = (ImageView) view.findViewById(R.id.ivPoster);
        tvTitleYear = (TextView) view.findViewById(R.id.tvTitleYear);
        tvCasts = (TextView) view.findViewById(R.id.tvCasts);
        tvRating = (TextView)view.findViewById(R.id.tvRating);
    }
}
