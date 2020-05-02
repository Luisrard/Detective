package com.example.detective;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "extraDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 2;
    private DatePicker mDatePicker;
    private  Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View vD= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();

        //Wiring Up
        calendar.setTime(mDate);
        mDatePicker = vD.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),null);
        return new AlertDialog.Builder(getActivity())
                .setView(vD)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDate.setYear(mDatePicker.getYear()-1900);
                                mDate.setMonth(mDatePicker.getMonth());
                                mDate.setDate(mDatePicker.getDayOfMonth());
                                //Launch Dialog of time
                                FragmentManager manager = getFragmentManager();
                                TimePickerFragment dialog2 = TimePickerFragment.newInstance(mDate);
                                dialog2.setTargetFragment(DatePickerFragment.this, REQUEST_TIME);
                                dialog2.show(manager, DIALOG_TIME);
                                //sendResult(Activity.RESULT_OK);
                            }
                })
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //We wait a result of timerpicker
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_TIME) {
            mDate = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
        }
        sendResult(Activity.RESULT_OK);
    }
    public static DatePickerFragment newInstance (Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
     }

     private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
     }
}
