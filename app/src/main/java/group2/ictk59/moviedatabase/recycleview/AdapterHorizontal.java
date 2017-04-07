package group2.ictk59.moviedatabase.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.model.Movie;

/**
 * Created by ZinZin on 3/31/2017.
 */

public class AdapterHorizontal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;

    private final int MOVIE = 0;
    private final int ACTOR = 1;

    private Context mContext;

    public AdapterHorizontal(Context context, List<Object> items) {
        this.items = items;
        mContext = context;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Movie) {
            return MOVIE;
        } else if (items.get(position) instanceof Actor) {
            return ACTOR;
        }
        return -1;
    }

    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MOVIE:
                View v1 = inflater.inflate(R.layout.movie_item_hor, viewGroup, false);
                viewHolder = new MovieViewHolderHorizontal(v1);
                break;
            case ACTOR:
                View v2 = inflater.inflate(R.layout.actor_item_hor, viewGroup, false);
                viewHolder = new ActorViewHolderHorizontal(v2);
                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case MOVIE:
                MovieViewHolderHorizontal vh1 = (MovieViewHolderHorizontal) viewHolder;
                configureMovieViewHolder(vh1, position);
                break;
            case ACTOR:
                ActorViewHolderHorizontal vh2 = (ActorViewHolderHorizontal) viewHolder;
                configureActorViewHolder(vh2, position);
                break;
        }
    }

    private void configureMovieViewHolder(MovieViewHolderHorizontal holder, int position) {
        Movie movie = (Movie) items.get(position);
        if (movie != null) {
            Picasso.with(mContext)
                    .load(movie.getPoster())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.ivPoster);

            holder.tvTitle.setText((position+1) + ". " + movie.getTitle());
        }
    }

    private void configureActorViewHolder(ActorViewHolderHorizontal holder, int position) {
        Actor actor = (Actor) items.get(position);
        if (actor != null){
            if (actor.getProfilePic() == null){
                actor.setProfilePic("");
            }
            Picasso.with(mContext)
                    .load("https://image.tmdb.org/t/p/w1000" + actor.getProfilePic())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.ivProfilePicture);
            holder.tvFullName.setText((position+1) + ". " + actor.getName());
        }
    }

    public void loadNewData(List<Object> newData){
        items = newData;
        notifyDataSetChanged();
    }
}
