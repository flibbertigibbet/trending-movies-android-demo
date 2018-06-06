package com.banderkat.trendingmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.banderkat.trendingmovies.trendingmovies.R;

public class MainActivity extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.main_activity_gridview);
        gridView.setAdapter(new MoviePosterAdapter(this));
    }
}
