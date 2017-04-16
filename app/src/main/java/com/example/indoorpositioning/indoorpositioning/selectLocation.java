package com.example.indoorpositioning.indoorpositioning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;


public class selectLocation extends AppCompatActivity {

    CoordinatorLayout mcoordinatorLayout;
    ListView lv;
    DatabaseHelper myDb;
    AlertDialog alertDialog;
    EditText locationName;
    EditText macaddr1;
    EditText macaddr2;
    EditText macaddr3;

    public void displaydata(){
        Cursor res = myDb.getAllData();
        lv = (ListView) findViewById(R.id.idListView);
        if(res.getCount() == 0){
            Snackbar snackbar = Snackbar.make(mcoordinatorLayout,"Add Locations To Get Started", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        ArrayList<String> buffer = new ArrayList<>();
        res.moveToFirst();
        try {
            do {
                buffer.add(res.getString(1));
            }while(res.moveToNext());
        }
        finally {
            res.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,buffer);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog deletealert = new AlertDialog.Builder(selectLocation.this).create();
                deletealert.setTitle("Delete");
                deletealert.setMessage("\nConfirm Delete?");
                deletealert.setButton(Dialog.BUTTON_POSITIVE,"YES",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int id, rowsAffected;
                        Cursor res = myDb.getAllData();
                        res.moveToPosition(position);
                        id = res.getInt(0);
                        rowsAffected = myDb.deleteData(id);
                        if(rowsAffected == 0){
                            Toast.makeText(getApplicationContext(),"Error: Could Not Delete Selected Item", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            //Removes animation from switching from one activity to another
                            overridePendingTransition(0,0);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Successfully Deleted Item", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
                deletealert.setButton(Dialog.BUTTON_NEGATIVE,"NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deletealert.show();
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                //Enter code to call another activity here
                Toast.makeText(getApplicationContext(),"Item Clicked: "+i,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void createAlertBox() {
        alertDialog = new AlertDialog.Builder(selectLocation.this).create();
        alertDialog.setTitle("Add a new Location");
        //alertDialog.setMessage("\nEnter Location Name");

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        locationName = new EditText(selectLocation.this);
        locationName.setHint("Enter Location Name:");
        locationName.setGravity(Gravity.LEFT);
        layout.addView(locationName);

        macaddr1 = new EditText(selectLocation.this);
        macaddr1.setHint("Enter MAC Address 1:");
        macaddr1.setGravity(Gravity.LEFT);
        layout.addView(macaddr1);

        macaddr2 = new EditText(selectLocation.this);
        macaddr2.setHint("Enter MAC Address 2:");
        macaddr2.setGravity(Gravity.LEFT);
        layout.addView(macaddr2);

        macaddr3 = new EditText(selectLocation.this);
        macaddr3.setHint("Enter MAC Address 3:");
        macaddr3.setGravity(Gravity.LEFT);
        layout.addView(macaddr3);

        alertDialog.setView(layout);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        mcoordinatorLayout = (CoordinatorLayout) findViewById(R.id.idLayout);
        myDb = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displaydata();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createAlertBox();


                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"ADD",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = locationName.getText().toString().trim();
                        String mac1 = macaddr1.getText().toString().trim();
                        String mac2 = macaddr2.getText().toString().trim();
                        String mac3 = macaddr3.getText().toString().trim();
                        if(name.equals("") || mac1.equals("") || mac2.equals("") || mac3.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Fill All Fields", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            boolean isInserted = myDb.insertData(name,mac1,mac2,mac3);
                            if(isInserted == true) {
                                Intent intent = getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0,0);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Error: Could Not Insert Data", Toast.LENGTH_LONG).show();
                        }
                        //db.close();
                        //finish();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"CANCEL",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
    }

}
