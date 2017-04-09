package com.example.indoorpositioning.indoorpositioning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class selectLocation extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        myDb = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final DBAdapter db = new DBAdapter(getApplicationContext());
        //final SQLiteDatabase mydatabase = openOrCreateDatabase("Locations",MODE_PRIVATE,null);
        //mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PrimaryLocations(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(selectLocation.this).create(); //Read Update
                alertDialog.setTitle("Add a new Location");
                //alertDialog.setMessage("\nEnter Location Name");

                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText locationName = new EditText(selectLocation.this);
                locationName.setHint("Enter Location Name:");
                locationName.setGravity(Gravity.LEFT);
                layout.addView(locationName);

                final EditText macaddr1 = new EditText(selectLocation.this);
                macaddr1.setHint("Enter MAC Address 1:");
                macaddr1.setGravity(Gravity.LEFT);
                layout.addView(macaddr1);

                final EditText macaddr2 = new EditText(selectLocation.this);
                macaddr2.setHint("Enter MAC Address 2:");
                macaddr2.setGravity(Gravity.LEFT);
                layout.addView(macaddr2);

                final EditText macaddr3 = new EditText(selectLocation.this);
                macaddr3.setHint("Enter MAC Address 3:");
                macaddr3.setGravity(Gravity.LEFT);
                layout.addView(macaddr3);

                alertDialog.setView(layout);

                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"ADD",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = locationName.getText().toString().trim();
                        String mac1 = macaddr1.getText().toString().trim();
                        String mac2 = macaddr2.getText().toString().trim();
                        String mac3 = macaddr3.getText().toString().trim();
                        if(name.equals("") || mac1.equals("") || mac2.equals("") || mac3.equals("")){
                            Toast.makeText(getApplicationContext(),"Please fill all fields", Toast.LENGTH_LONG).show();
                            return;
                        }
                       // db.open();
                        //db.insertRow(name, mac1, mac2, mac3);
                        //mydatabase.execSQL("INSERT INTO PrimaryLocations VALUE('"+name+"');");
                        Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG).show();
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
