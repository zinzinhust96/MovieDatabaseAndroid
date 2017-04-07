package group2.ictk59.moviedatabase.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/29/2017.
 */

public class ActorViewHolder extends RecyclerView.ViewHolder {
    protected ImageView ivProfilePicture;
    protected TextView tvFullName;
    protected TextView tvKnownFor;

    public ActorViewHolder(View view) {
        super(view);
        ivProfilePicture = (ImageView)view.findViewById(R.id.ivProfilePicture);
        tvFullName = (TextView)view.findViewById(R.id.tvFullName);
        tvKnownFor = (TextView)view.findViewById(R.id.tvKnownFor);
    }
}
