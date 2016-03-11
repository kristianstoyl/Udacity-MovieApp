package com.ntnu.kristian.movieapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private AndroidFlavor poster;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null){
            // receives the poster object from intent
            poster = intent.getParcelableExtra("movieTag");
            // initializes imageview from fragment_detail
            ImageView imgView = (ImageView) rootView.findViewById(R.id.detail_imageView);
            // base url, common for all movieposters
            // w780 size, bigger is always better! (assuming you have fast internet)
            String baseUrl = "http://image.tmdb.org/t/p/w780";
            // Uses picasso library to load image from url to imageview
            Picasso.with(getContext()).load(baseUrl + poster.versionNumber).into(imgView);
        }
        return rootView;
    }
}
