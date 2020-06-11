package com.example.detective;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getExternalCacheDirs;
import static androidx.core.content.ContextCompat.getExternalFilesDirs;


public class CrimeFragment extends Fragment {
    private Crime mCrime    ;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mSeriousCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mImageCrime;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_TAKE_PHOTO = 5;

    private final String FOLDER_ROOT = "imageDetective/";
    private final String ROUT_IMAGE = FOLDER_ROOT + "mCrimes";
    private String path;
    private boolean mTakePhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        mCrime = crimeLab.getCrime(crimeId);
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
        mTakePhoto = validatePermission();
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
        //Button to send the report
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


        //Image button show the image crime
        mImageCrime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mTakePhoto)
                    chargeImage();
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
        else  if(requestCode == REQUEST_PHOTO && data != null) {
            Uri mPath = data.getData();
            mImageCrime.setImageURI(mPath);
        }
        else if(requestCode == REQUEST_TAKE_PHOTO){
            MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            mImageCrime.setImageBitmap(bitmap);
                        }
                    });
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



    //Take photo

    @SuppressLint("IntentReset")
    private void chargeImage() {
        final CharSequence[] options = {"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOption = new AlertDialog.Builder(getContext());
        alertOption.setTitle("Selecciona una opción");
        alertOption.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar Foto")){
                    try {
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(options[which].equals("Cargar Imagen")){
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(Intent.createChooser(intent, "Select the app"),
                            REQUEST_PHOTO);
                }
                else{
                    dialog.dismiss();
                }
            }
        });
        alertOption.show();
    }

    private void takePhoto() throws IOException {
        File image = File.createTempFile(mCrime.getPhotoFileName(),".jpg");
        path = image.getPath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(){

        }
        startActivityForResult(intent,REQUEST_TAKE_PHOTO);
    }

    private boolean validatePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);

        }
        return false;
    }

}

