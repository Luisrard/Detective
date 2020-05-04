package com.example.detective;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "crimeId";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mButtonFirst;
    private Button mButtonLast;
    private int mPage;          //Is used for validation of changes
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        //Wiring up
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.crime_view_pager);
        mButtonFirst = findViewById(R.id.first_crime_button);
        mButtonLast = findViewById(R.id.last_crime_button);
        mCrimes = CrimeLab.get(this).getCrimes();

        fragmentManager = getSupportFragmentManager();

        setAdapter();

        getPosition(crimeId);
        verificationPosition();

        mButtonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size()-1);//Go to the last crime
            }
        });
        mButtonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);//Go to the first crime
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {//For Page Change
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(mPage != position) {//If the current item is different of position
                    verificationPosition();
                    mPage=position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_crime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                showDialog(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void getPosition(UUID idCrime) {//Function to get the position through an Id
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getmId().equals(idCrime)) {
                mViewPager.setCurrentItem(i);
                mPage = mViewPager.getCurrentItem();
                break;
            }
        }
    }
    public void verificationPosition(){
        if(mViewPager.getCurrentItem() == 0){//If the first item is showed enabled the button
            mButtonFirst.setEnabled(false);
        }
        else{
            mButtonFirst.setEnabled(true);
        }

        if(mViewPager.getCurrentItem() == mCrimes.size()-1){//If the Last item is showed enabled the button
            mButtonLast.setEnabled(false);
        }
        else{
            mButtonLast.setEnabled(true);
        }
    }
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
    private void showDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(CrimePagerActivity.this);
        builder.setTitle("Warning")
                .setMessage("Are you sure you want to delete this crime?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CrimeLab crimeLab = CrimeLab.get(context);
                        crimeLab.deleteCrime(mCrimes.get(mPage).getmId());
                        mCrimes = crimeLab.getCrimes();//Assigning values
                        if(mCrimes.size()>0) {//If exist one or more crimes send the method setAdapter to update
                            if(mCrimes.size() > 1 && mPage > 0){//This is for move to the left
                                mPage--;
                            }
                            else{
                                mPage = 0;
                            }
                            reiniciarActivity((Activity) context,mCrimes.get(mPage).getmId());
                        }
                        else{//Else finish the activity
                            finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void setAdapter() {
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getmId());
            }
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mPage = mViewPager.getCurrentItem();
    }

    public static void reiniciarActivity(Activity actividad, UUID id){
        Intent intent = CrimePagerActivity.newIntent(actividad, id);
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }
}
