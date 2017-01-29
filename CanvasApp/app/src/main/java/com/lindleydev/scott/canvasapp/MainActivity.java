package com.lindleydev.scott.canvasapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private DrawingView mDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDrawingView.getThread() != null && mDrawingView.getThread().isRunning()) {
            mDrawingView.getThread().setRunning(false);
        }
        ((RelativeLayout)findViewById(R.id.layout)).removeAllViews();
    }

    @Override
    protected void onResume() {
        setContentView(R.layout.activity_main);
        mDrawingView = (DrawingView)findViewById(R.id.drawing_view);
        super.onResume();
    }
}
