package com.ntnu.kristian.movieapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by Kristian on 27.02.2016.
 */
public class AndroidFlavor implements Parcelable{
    String versionName;
    String versionNumber;
    int image; // drawable reference id

    public AndroidFlavor(String vName, String vNumber, int image)
    {
        this.versionName = vName;
        this.versionNumber = vNumber;
        this.image = image;
    }

    // Parcel part
    protected AndroidFlavor(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.versionName = data[0];
        this.versionNumber = data[1];
        this.image = Integer.parseInt(data[2]);
    }

    public static final Creator<AndroidFlavor> CREATOR = new Creator<AndroidFlavor>() {
        @Override
        public AndroidFlavor createFromParcel(Parcel in) {
            return new AndroidFlavor(in);
        }

        @Override
        public AndroidFlavor[] newArray(int size) {
            return new AndroidFlavor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{this.versionName, this.versionNumber
        , String.valueOf(this.image)});
    }
}