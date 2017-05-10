package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.model.Movie;
import group2.ictk59.moviedatabase.recycleview.AdapterHorizontal;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 4/13/2017.
 */

public class ActorProfileFragment extends Fragment implements RecyclerViewClickListener{

    AdapterHorizontal mAdapter;

    ExpandableTextView etvBio;
    TextView tvFullName, tvPopularity, tvBirthday, tvPlaceOfBirth, tvDeathday;
    ImageView ivProfilePicture;
    RecyclerView rvMovieList;
    LinearLayout llDeathDay;
    ProgressDialog progressDialog;

    private List knownFor;

    OnItemSelectedListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;

        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actorprofile, container, false);
        setUpView(rootView);

        return rootView;
    }

    private void setUpView(View view){
        etvBio = (ExpandableTextView)view.findViewById(R.id.expand_text_view);
        tvFullName = (TextView)view.findViewById(R.id.tvFullName);
        tvPopularity = (TextView)view.findViewById(R.id.tvPopularity);
        tvBirthday = (TextView)view.findViewById(R.id.tvBirthday);
        tvPlaceOfBirth = (TextView)view.findViewById(R.id.tvPlaceOfBirth);
        tvDeathday = (TextView)view.findViewById(R.id.tvDeathday);
        ivProfilePicture = (ImageView)view.findViewById(R.id.ivProfilePicture);
        llDeathDay = (LinearLayout)view.findViewById(R.id.llDeathDay);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvMovieList = (RecyclerView)view.findViewById(R.id.rvMovieList);
        rvMovieList.setLayoutManager(layoutManager);
        mAdapter = new AdapterHorizontal(getActivity(), new ArrayList<>(), this);
        rvMovieList.setAdapter(mAdapter);
    }

    private void updateView(Actor actor){

        etvBio.setText(actor.getBiography().equalsIgnoreCase("null") ? "" : actor.getBiography());
        tvFullName.setText(actor.getName());
        tvPopularity.setText(String.format(Locale.US, "%f", actor.getPopularity()));

        DateFormat fromData = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat myFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        try {
            Date birthday = fromData.parse(actor.getBirthday());
            if (actor.getDeathday().isEmpty()){
                Date currentDate = new Date();
                int age = getDiffYears(birthday, currentDate);
                tvBirthday.setText(myFormat.format(birthday) + " (age " + age + ")");
            }else{
                Date deathday = fromData.parse(actor.getDeathday());
                int age = getDiffYears(birthday, deathday);
                tvDeathday.setVisibility(View.VISIBLE);
                tvBirthday.setText(myFormat.format(birthday));
                llDeathDay.setVisibility(View.VISIBLE);
                tvDeathday.setText(myFormat.format(deathday) + " (age " + age + ")");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvPlaceOfBirth.setText(actor.getPlaceOfBirth().equalsIgnoreCase("null") ? "" : actor.getPlaceOfBirth());
        if (actor.getProfilePic() == null){
            actor.setProfilePic("");
        }
        Picasso.with(getActivity())
                .load("https://image.tmdb.org/t/p/w1000" + actor.getProfilePic())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(ivProfilePicture);
        knownFor = (List)actor.getKnownFor();
        mAdapter.loadNewData(knownFor);
    }

    private int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    private Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    @Override
    public void onResume() {
        super.onResume();
        Long id = getArguments().getLong(Constants.ID);

        ProcessActor processActor = new ProcessActor(id);
        processActor.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Actor Profile");
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Movie)knownFor.get(position)).getId();
        mCallback.onMovieSelected(id);
    }

    @Override
    public void onViewClicked(View v, int position) {
        final Long id = ((Movie)knownFor.get(position)).getId();
        if (v.getId() == R.id.ivAdd){
            mCallback.onViewAddSelected(id);
        }else if (v.getId() == R.id.ivRemove){
            mCallback.onViewRemoveSelected(id);
        }
    }

    public class ProcessActor extends GetActorJsonData {
        public ProcessActor(Long id) {
            super(id);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getActivity(), "", "Retrieving latest data...", true);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                List actors = getActors();
                Actor actor = (Actor)actors.get(0);
                updateView(actor);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
