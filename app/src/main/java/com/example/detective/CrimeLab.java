package com.example.detective;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    private CrimeLab(Context context){
        mCrimes = new ArrayList<>();
        for(int i = 0; i<100; i++){
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            mCrimes.add(crime);

        }
    }
    private CrimeLab(Context context, String city){
        mCrimes = new ArrayList<>();
        for(int i = 0; i<100; i++){
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            crime.setmSerious(i % 2 != 0);
            crime.setmCity(city);
            mCrimes.add(crime);
        }
    }

    public static CrimeLab get(Context context , String city){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context, city);
        }
        return sCrimeLab;
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        int i = mCrimes.size()/2;
        for(Crime crime : mCrimes){
            if (crime.getmId().equals(id)){
                return crime;
            }
        }
        return null;
    }
}
