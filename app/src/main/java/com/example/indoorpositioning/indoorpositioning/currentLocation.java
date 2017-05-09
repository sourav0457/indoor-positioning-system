package com.example.indoorpositioning.indoorpositioning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class currentLocation extends AppCompatActivity {

    CoordinatorLayout mcoordinatorLayout;
    ListView lv;
    DatabaseHelper myDb;
    //int locationId;
    //Cursor trainingSet;
    JSONArray resultSet = new JSONArray();

    /*public void senddata(){
        try {
            URL url = new URL("http://192.168.1.9:8000/trainingset/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(resultSet.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(),"Sent Data Successfully!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Not Successfully!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public void cur2json(){
        trainingSet.moveToFirst();
        while(trainingSet.isAfterLast() == false){
            int totalColumn = trainingSet.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (trainingSet.getColumnName(i) != null) {
                    try {
                        rowObject.put(trainingSet.getColumnName(i),     //JSON objects are similar to dictionaries
                                trainingSet.getInt(i));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            resultSet.put(rowObject);   //Structure of JSON array is similar to lists in python (it contains a list of json objects)
            trainingSet.moveToNext();
        }

        trainingSet.close();
    }*/

    public void displaydata(){
        final Cursor res = myDb.getAllData();
        lv = (ListView) findViewById(R.id.idListView);
        if(res.getCount() == 0){
            Snackbar snackbar = Snackbar.make(mcoordinatorLayout,"Add Training Set Data To Get Started", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        final ArrayList<Integer> arrayId = new ArrayList<>();
        ArrayList<String> buffer = new ArrayList<>();
        res.moveToFirst();
        try {
            do {
                arrayId.add(res.getInt(0));
                buffer.add(res.getString(1));
            }while(res.moveToNext());
        }
        finally {
            res.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,buffer);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                //locationId = arrayId.get(i);
                Toast.makeText(getApplicationContext(),"Item Clicked: "+ arrayId.get(i),Toast.LENGTH_SHORT).show();
                //trainingSet = myDb.getAllDataTraining(locationId);
                //cur2json();
                //senddata();
                Intent intent = new Intent(getApplicationContext(), DisplayLocation.class);
                intent.putExtra("id", arrayId.get(i));
                startActivity(intent);

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        mcoordinatorLayout = (CoordinatorLayout) findViewById(R.id.idLayout);
        myDb = new DatabaseHelper(this);

        displaydata();
    }
}
