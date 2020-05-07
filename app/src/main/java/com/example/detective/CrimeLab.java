package com.example.detective;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.detective.CrimeDbSchema.*;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private static String mCity;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab (Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
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

    List<Crime> getCrimes() {//using the DB
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = (CrimeCursorWrapper) queryCrimes(null,null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursorWrapper = (CrimeCursorWrapper) queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
                );
        try {
            if (cursorWrapper.getCount() == 0){
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }
        finally {
            cursorWrapper.close();
        }
    }
    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null,values);
    }

    public void deleteCrime(UUID id){
        deleteCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()});
    }
    private void deleteCrimes(String whereClause, String[] whereArgs){
        mDatabase.delete(
                CrimeTable.NAME,
                whereClause,
                whereArgs
        );
    }

    public void updateCrime (Crime crime){
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",new String[] {uuidString});
    }

    private CursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                            CrimeTable.NAME,
                            null, //Select columns - null selects all columns
                            whereClause,
                            whereArgs,
                            null,
                            null,
                            null
                            );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues (Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getmTitle());
        values.put(CrimeTable.Cols.DATE, crime.getmDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.ismSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SERIOUS, crime.ismSerious() ? 1 : 0);
        values.put(CrimeTable.Cols.CITY, crime.getmCity());
        return values;
    }
}
