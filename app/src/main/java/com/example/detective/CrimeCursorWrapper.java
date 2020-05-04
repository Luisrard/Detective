package com.example.detective;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.detective.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String city = getString(getColumnIndex(CrimeTable.Cols.CITY));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int serious = getInt(getColumnIndex(CrimeTable.Cols.SERIOUS));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setmTitle(title);
        crime.setmCity(city);
        crime.setmDate(new Date(date));
        crime.setmSolved(solved != 0);
        crime.setmSerious(serious != 0);

        return crime;
    }
}
