package group2.ictk59.moviedatabase.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 4/1/2017.
 */

public class ActorViewHolderHorizontal extends RecyclerView.ViewHolder {
    protected ImageView ivProfilePicture;
    protected TextView tvFullName;

    public ActorViewHolderHorizontal(View view) {
        super(view);
        ivProfilePicture = (ImageView)view.findViewById(R.id.ivProfilePicture);
        tvFullName = (TextView)view.findViewById(R.id.tvFullName);
    }
}
