package com.ntnu.kristian.movieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    // MovieDB api url + key:
    // https://api.themoviedb.org/3/movie/550?api_key=5aa5bc75c39f6d200fa6bd741896baaa

    //Movie poster url example:
    // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    // http://image.tmdb.org/t/p/<size>/<image.jpg>
    // Sizes ranges from:  "w92", "w154", "w185", "w342", "w500", "w780",
    // or "original". For most phones we recommend using “w185”.

    // How to load images with Picasso
    //Picasso.with(getContext()).load(url).into(imageView);

    private AndroidFlavorAdapter mImageAdapter;
    private AndroidFlavorAdapter mMovieAdapter;
    AndroidFlavor[] androidFlavors = {
            new AndroidFlavor("Cupcake", "1.5", R.drawable.cupcake),
            new AndroidFlavor("Donut", "1.6", R.drawable.donut),
            new AndroidFlavor("Eclair", "2.0-2.1", R.drawable.eclair),
            new AndroidFlavor("Froyo", "2.2-2.2.3", R.drawable.froyo),
            new AndroidFlavor("GingerBread", "2.3-2.3.7", R.drawable.gingerbread),
            new AndroidFlavor("Honeycomb", "3.0-3.2.6", R.drawable.honeycomb),
            new AndroidFlavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.icecream),
            new AndroidFlavor("Jelly Bean", "4.1-4.3.1", R.drawable.jellybean),
            new AndroidFlavor("KitKat", "4.4-4.4.4", R.drawable.kitkat),
            new AndroidFlavor("Lollipop", "5.0-5.1.1", R.drawable.lollipop)
    };


    public MainActivityFragment() {
    }

    @Override
    public void onStart(){
        super.onStart();
        updatePosters();
    }
    public void updatePosters(){
        FetchPosterTask posterTask = new FetchPosterTask();
        posterTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        /*
        mMovieAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_movie, // The name of the layout ID.
                        R.id.list_item_movie_imageview, // The ID of the textview to populate.
                        new ArrayList<String>());
                        */
        mImageAdapter = new AndroidFlavorAdapter(getActivity(), Arrays.asList(androidFlavors));
        mMovieAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList<AndroidFlavor>());
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AndroidFlavor poster = mImageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, poster.image);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public class FetchPosterTask extends AsyncTask<String, Void, AndroidFlavor[]>{
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected AndroidFlavor[] doInBackground(String... params){
            // JsonList
            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5aa5bc75c39f6d200fa6bd741896baaa

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String appId = "5aa5bc75c39f6d200fa6bd741896baaa";
            String posterJsonStr = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5aa5bc75c39f6d200fa6bd741896baaa");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                posterJsonStr = buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getPosterDataFromJson(posterJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private AndroidFlavor[] getPosterDataFromJson(String json)
                throws JSONException {
                //

            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "title";
            final String OWM_NUMBER = "poster_path";
            final String OWM_IMAGE = "id";

            JSONObject posterJson = new JSONObject(json);
            JSONArray posterArray = posterJson.getJSONArray(OWM_RESULTS);

            AndroidFlavor[] posterList = new AndroidFlavor[20];

            for(int i = 0; i < posterArray.length(); i++){
                String name;
                String number;
                int image;

                JSONObject movie = posterArray.getJSONObject(i);

                name = movie.getString(OWM_TITLE);
                number = movie.getString(OWM_NUMBER);
                image = movie.getInt(OWM_IMAGE);
                posterList[i] = new AndroidFlavor(name, number, image);
            }
            return posterList;
        }

        @Override
        protected void onPostExecute(AndroidFlavor[] result){
            if(result != null){
                mMovieAdapter.clear();
                for(AndroidFlavor movieList : result){
                    String s = "..";
                   mMovieAdapter.add(movieList);
                }
            }
        }
    }
}
