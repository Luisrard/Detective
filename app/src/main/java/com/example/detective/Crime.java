package com.example.detective;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private boolean mSerious;
    private Date mDate;
    private boolean mSolved;
    private String mCity;
    private String mSuspect;

    public String getmSuspect() {
        return mSuspect;
    }

    public void setmSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
        mCity = CrimeLab.getmCity();
    }

    public Crime(UUID id){
        mId = id;
    }

    public UUID getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDateFormat(){//Function to convert a Class Date to a String of format time "Day Month, Hours and minutes, City"
        String mFormat = "dd MMMM, HH:mm:ss, ";
        SimpleDateFormat vFormatDate = new SimpleDateFormat(mFormat, new Locale("EN","US"));
        String vDate = vFormatDate.format(mDate) + mCity;
        return vDate;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmCity() {
        return mCity;
    }
    
    public boolean ismSerious() {
        return mSerious;
    }

    public void setmSerious(boolean mSerious) {
        this.mSerious = mSerious;
    }

    public String getPhotoFileName(){
        return mId.toString() + ".jpg";
    }
}

