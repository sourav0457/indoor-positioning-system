package com.example.indoorpositioning.indoorpositioning;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void collectTrainingSet(View view)
    {
        Intent intent = new Intent(this, selectLocation.class);
        startActivity(intent);
    }

    public void locateMe(View view)
    {
        Intent intent = new Intent(this, currentLocation.class);
        startActivity(intent);
    }

    public void getAccessPoints(View view)
    {
        Intent intent = new Intent(this, AccessPoints.class);
        Toast.makeText(getApplicationContext(),"Fetching Data...",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void exit(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        else {
            this.finishAffinity();
        }
        System.exit(0);
    }
}
