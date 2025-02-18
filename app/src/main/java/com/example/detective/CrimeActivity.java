package com.example.detective;


import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;


public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "crimeID";

    public static Intent newIntent(Context packageContext, UUID crimeID){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
