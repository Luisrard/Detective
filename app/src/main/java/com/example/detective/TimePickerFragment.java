package com.example.detective;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "extraTime";
    private TimePicker mTimePicker;
    private Date mDate;
    private EditText mSeconds;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View vT= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        if (getArguments() != null) {
            mDate= (Date) getArguments().getSerializable(ARG_TIME);
        }
        //Wiring Up

        mSeconds = vT.findViewById(R.id.editText);
        mTimePicker = vT.findViewById(R.id.dialog_time_picker);
        mTimePicker.setHour(mDate.getHours());
        mTimePicker.setHour(mDate.getHours());
        mSeconds.setText(String.valueOf(mDate.getSeconds()));

        return new AlertDialog.Builder(getActivity())
                .setView(vT)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mDate.setHours(mTimePicker.getHour());
                                mDate.setMinutes(mTimePicker.getMinute());
                                if(mSeconds.getText()!=null) {
                                    try {
                                        //if the number in mSecond is greater than 60 you get the rest of that number
                                        mDate.setSeconds(Integer.parseInt(mSeconds.getText().toString()) % 60);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        })
                .create();
    }

    @Override
    public void onDestroyView() {//If we have a problem or not we send the info
        super.onDestroyView();
        sendResult(Activity.RESULT_OK);
    }

    public static TimePickerFragment newInstance (Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
