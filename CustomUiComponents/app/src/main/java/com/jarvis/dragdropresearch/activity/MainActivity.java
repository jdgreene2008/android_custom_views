package com.jarvis.dragdropresearch.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.jarvis.dragdropresearch.R;
import com.jarvis.dragdropresearch.views.AbsCustomScrollingView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AbsCustomScrollingView mGestureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestureView = findViewById(R.id.gesture_view);
        mGestureView.setListener(new AbsCustomScrollingView.ScrollListener() {
            @Override
            public void onDragStart() {
                Toast.makeText(MainActivity.this, "Drag Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDragEnd() {
                Toast.makeText(MainActivity.this, "Drag Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
