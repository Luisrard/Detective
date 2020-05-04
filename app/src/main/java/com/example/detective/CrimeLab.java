package com.example.detective;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.detective.CrimeDbSchema.*;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private static String mCity;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab (Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        mCrimes = new ArrayList<>();
    }

    public static String getmCity() {
        return mCity;
    }

    public static void setmCity(String mCity) {
        CrimeLab.mCity = mCity;
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for (Crime crime : mCrimes){
            if(crime.getmId().equals(id)){
                return crime;
            }
        }
        return null;
    }
    public void addCrime(Crime c){
        mCrimes.add(c);
        //ContentValues values = getContentValues(c);
        //mDatabase.insert(CrimeTable.NAME, null,values);
    }
    public void deleteCrime(UUID id){
        int index=0;
        for(Crime mCrime : mCrimes){
            if (mCrime.getmId().equals(id)){
                break;
            }
            index++;
        }
        mCrimes.remove(index);
    }

    public void updateCrime (Crime crime){
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",new String[] {uuidString});
    }

    private static ContentValues getContentValues (Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getmTitle());
        values.put(CrimeTable.Cols.DATE, crime.getmDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.ismSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SERIOUS, crime.ismSerious() ? 1 : 0);

        return values;
    }
}
