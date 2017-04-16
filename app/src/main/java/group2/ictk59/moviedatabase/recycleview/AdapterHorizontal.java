package group2.ictk59.moviedatabase.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
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
    private RecyclerViewClickListener mListener;

    public AdapterHorizontal(Context context, List<Object> items, RecyclerViewClickListener listener) {
        this.items = items;
        mContext = context;
        mListener = listener;
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
            if (RESTServiceApplication.getInstance().isLogin()){
                List<Long> watchlistIds = RESTServiceApplication.getInstance().getWatchlistId();
                if (watchlistIds != null){
                    if (watchlistIds.contains(movie.getId())){
                        holder.ivAdd.setVisibility(View.GONE);
                        holder.ivRemove.setVisibility(View.VISIBLE);
                    }else{
                        holder.ivRemove.setVisibility(View.GONE);
                        holder.ivAdd.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                holder.ivRemove.setVisibility(View.GONE);
                holder.ivAdd.setVisibility(View.VISIBLE);
            }
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

    public Object getListItem(int position){
        return (null != items? items.get(position) : null);
    }

    public class MovieViewHolderHorizontal extends RecyclerView.ViewHolder {
        protected ImageView ivPoster, ivAdd, ivRemove;
        protected TextView tvTitle;

        public MovieViewHolderHorizontal(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onRowClicked(getAdapterPosition());
                    }
                }
            });

            ivPoster = (ImageView) view.findViewById(R.id.ivPoster);
            ivAdd = (ImageView)view.findViewById(R.id.ivAdd);
            ivRemove = (ImageView)view.findViewById(R.id.ivRemove);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onViewClicked(v, getAdapterPosition());
                        if (RESTServiceApplication.getInstance().isLogin()) {
                            Toast.makeText(mContext, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                            ivAdd.setVisibility(View.GONE);
                            ivRemove.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onViewClicked(v, getAdapterPosition());
                        ivRemove.setVisibility(View.GONE);
                        ivAdd.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public class ActorViewHolderHorizontal extends RecyclerView.ViewHolder {
        protected ImageView ivProfilePicture;
        protected TextView tvFullName;

        public ActorViewHolderHorizontal(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onRowClicked(getAdapterPosition());
                    }
                }
            });

            ivProfilePicture = (ImageView)view.findViewById(R.id.ivProfilePicture);
            tvFullName = (TextView)view.findViewById(R.id.tvFullName);
        }
    }
}
