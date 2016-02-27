package com.ntnu.kristian.movieapp;

import android.widget.ImageView;

/**
 * Created by Kristian on 27.02.2016.
 */
public class AndroidFlavor {
    String versionName;
    String versionNumber;
    int image; // drawable reference id

    public AndroidFlavor(String vName, String vNumber, int image)
    {
        this.versionName = vName;
        this.versionNumber = vNumber;
        this.image = image;
    }

}