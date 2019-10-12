package com.jarvis.dragdropresearch.activity;

import android.os.Bundle;

import com.jarvis.dragdropresearch.R;

import androidx.appcompat.app.AppCompatActivity;

public class CustomScrollingNavHostActivity extends AppCompatActivity {

    private static final String TAG = CustomScrollingNavHostActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scrolling_nav_host);
    }
}
