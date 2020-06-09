package com.example.detective;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment {
    private Crime mCrime    ;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mSeriousCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mImageCrime;
    private File mPhotoFile;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_DATE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        mCrime = crimeLab.getCrime(crimeId);
        mPhotoFile = crimeLab.getPhotoFile(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container,false);
        //Wiring up
        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSeriousCheckBox = v.findViewById(R.id.crime_serious);
        mReportButton = v.findViewById(R.id.crime_report);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mImageCrime = v.findViewById(R.id.crime_image);
        mSeriousCheckBox.setChecked(mCrime.ismSerious());
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mTitleField.setText(mCrime.getmTitle());
        mDateButton.setText(mCrime.getDateFormat());

        printField();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setmTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setmSolved(isChecked);
            }
        });
        mSeriousCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setmSerious(isChecked);
                printField();
            }
        });
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        final Intent captureImage   = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCrime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.detective.FileP rovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().
                        queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.toString(),uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_CAMERA);
                return false;
            }
        });
        if(mCrime.getmSuspect() != null){
            mSuspectButton.setText(mCrime.getmSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, packageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }


        boolean canTakePhoto = (mPhotoFile != null && captureImage.resolveActivity(packageManager) != null);
        mImageCrime.setEnabled(canTakePhoto);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            mDateButton.setText(mCrime.getDateFormat());
        }
        else if(requestCode == REQUEST_CONTACT && data != null){
            Uri contact = data.getData();
            String [] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Create query to show the suspect
            Cursor cursor = getActivity().getContentResolver().query(contact,queryFields,
                    null,null,null);
            try {
                if(cursor.getCount() == 0){
                    return;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setmSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                cursor.close();
            }
        }
    }

    private String getCrimeReport(){
        String solvedString;
        if(mCrime.ismSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateString = mCrime.getDateFormat();
        String suspect = mCrime.getmSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else{
            suspect = getString(R.string.crime_report_suspect,suspect);
        }
        String report = getString(R.string.crime_report,mCrime.getmTitle(),dateString,solvedString, suspect);
        return report;
    }

    private void printField(){
        if(mCrime.ismSerious()) {
            mTitleField.setTextColor(getResources().getColor(R.color.colorRed));
        }
        else{
            mTitleField.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
}

