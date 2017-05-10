package group2.ictk59.moviedatabase.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.model.Actor;
import group2.ictk59.moviedatabase.recycleview.RatedMovieListRecyclerViewAdapter;
import group2.ictk59.moviedatabase.recycleview.RecyclerViewClickListener;

/**
 * Created by ZinZin on 5/10/2017.
 */

public class BornTodayFragment extends Fragment implements RecyclerViewClickListener {

    private static RecyclerView rvActorList;
    static TextView tvMonthDay;
    LinearLayout datePickerLayout;
    private static TextView tvNoResults;
    private static ArrayList<Object> items;
    private static RatedMovieListRecyclerViewAdapter mAdapter;
    OnItemSelectedListener mCallback;
    private static ProgressBar progressBar;

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
        View rootView = inflater.inflate(R.layout.fragment_borntoday, container, false);
        setUpView(rootView);

        return rootView;
    }

    private void setUpView(View view){
        rvActorList = (RecyclerView)view.findViewById(R.id.rvActorList);
        rvActorList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mAdapter = new RatedMovieListRecyclerViewAdapter(getActivity(), new ArrayList<Object>(), this);
        rvActorList.setAdapter(mAdapter);
        tvMonthDay = (TextView)view.findViewById(R.id.tvMonthDay);
        tvNoResults = (TextView)view.findViewById(R.id.tvNoResults);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        datePickerLayout = (LinearLayout)view.findViewById(R.id.datePickerLayout);
        datePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        Calendar today = Calendar.getInstance();
        setTVMonthDay(today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1);
    }

    @Override
    public void onRowClicked(int position) {
        Long id = ((Actor)mAdapter.getListItem(position)).getId();
        mCallback.onActorSelected(id);
    }

    @Override
    public void onViewClicked(View v, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        items = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        ProcessActorList processActorList = new ProcessActorList(formatDateToString(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1));
        processActorList.execute();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Born Today");
    }

    public static void setTVMonthDay(int day, int month){
        DateFormat fromData = new SimpleDateFormat("dd-MM", Locale.US);
        DateFormat myFormat = new SimpleDateFormat("MMMM dd", Locale.US);
        try {
            Date date = fromData.parse(day + "-" + month);
            BornTodayFragment.tvMonthDay.setText(myFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String formatDateToString(int day, int month){
        DateFormat fromData = new SimpleDateFormat("dd-MM", Locale.US);
        DateFormat myFormat = new SimpleDateFormat("MM-dd", Locale.US);
        try {
            Date date = fromData.parse(day + "-" + month);
            String result = myFormat.format(date);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, this, year, month, day);
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE);
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            BornTodayFragment.setTVMonthDay(dayOfMonth, monthOfYear+1);
            items.clear();
            tvNoResults.setVisibility(View.GONE);
            ProcessActorList processActorList = new ProcessActorList(BornTodayFragment.formatDateToString(dayOfMonth, monthOfYear+1));
            processActorList.execute();
        }
    }

    private static class ProcessActorList extends GetActorJsonData {
        private ProcessActorList(String birthday) {
            super(birthday, true);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        private class ProcessData extends DownloadJsonData {

            @Override
            protected void onPreExecute() {
                rvActorList.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                items.addAll(getActors());
                if (items.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    mAdapter.loadNewData(items);
                }
                rvActorList.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }

}
