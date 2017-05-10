package group2.ictk59.moviedatabase.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 5/10/2017.
 */

public class BornTodayFragment extends Fragment {

    RecyclerView rvActorList;
    static TextView tvMonthDay;
    LinearLayout datePickerLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_borntoday, container, false);
        setUpView(rootView);

        return rootView;
    }

    private void setUpView(View view){
        rvActorList = (RecyclerView)view.findViewById(R.id.rvActorList);
        tvMonthDay = (TextView)view.findViewById(R.id.tvMonthDay);
        datePickerLayout = (LinearLayout)view.findViewById(R.id.datePickerLayout);
        datePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        Calendar today = Calendar.getInstance();
        formatDate(today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Born Today");
    }

    public static void formatDate(int day, int month){
        DateFormat fromData = new SimpleDateFormat("dd-MM");
        DateFormat myFormat = new SimpleDateFormat("MMMM dd", Locale.US);
        try {
            Date date = fromData.parse(day + "-" + month);
            BornTodayFragment.tvMonthDay.setText(myFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            BornTodayFragment.formatDate(dayOfMonth, monthOfYear+1);

        }
    }

}
