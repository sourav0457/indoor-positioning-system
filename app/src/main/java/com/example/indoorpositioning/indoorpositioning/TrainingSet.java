package com.example.indoorpositioning.indoorpositioning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class TrainingSet extends AppCompatActivity {

    CoordinatorLayout mcoordinatorLayout;
    DatabaseHelper myDb;
    GridView gv;
    AlertDialog alertDialog;
    EditText xparam;
    EditText yparam;
    int intentId;

    public void displaydata(){
        Cursor res = myDb.getXYDataTraining(intentId);
        gv = (GridView)findViewById(R.id.idGridView);
        if(res.getCount() == 0){
            Snackbar snackbar = Snackbar.make(mcoordinatorLayout,"Add Training Data To Get Started", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        ArrayList<String> buffer = new ArrayList<>();
        res.moveToFirst();
        try {
            do {
                buffer.add("( X : " + res.getInt(0) + ", Y : " + res.getInt(1) + " )");
            }while(res.moveToNext());
        }
        finally {
            res.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,buffer);
        gv.setAdapter(adapter);
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog deleteAlert = new AlertDialog.Builder(TrainingSet.this).create();
                deleteAlert.setTitle("Delete");
                deleteAlert.setMessage("\nConfirm Delete?");
                deleteAlert.setButton(Dialog.BUTTON_POSITIVE,"YES",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int id, rowsAffected;
                        Cursor res = myDb.getAllDataTraining(intentId);
                        res.moveToPosition(position);
                        id = res.getInt(0);
                        rowsAffected = myDb.deleteTrainingData(id);
                        if(rowsAffected == 0){
                            Toast.makeText(getApplicationContext(),"Error: Could Not Delete Selected Item", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            //Removes animation when switching from one activity to another
                            overridePendingTransition(0,0);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Successfully Deleted Item", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
                deleteAlert.setButton(Dialog.BUTTON_NEGATIVE,"NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteAlert.show();
                return false;
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

                //Enter code to call another activity here
                Toast.makeText(getApplicationContext(),"Item Clicked: "+i,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void createAlertBox() {
        alertDialog = new AlertDialog.Builder(TrainingSet.this).create();
        alertDialog.setTitle("Enter Your X and Y Co-ordinates");
        //alertDialog.setMessage("\nEnter Location Name");

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        xparam = new EditText(TrainingSet.this);
        xparam.setHint("X Co-ordinate");
        xparam.setGravity(Gravity.LEFT);
        layout.addView(xparam);

        yparam = new EditText(TrainingSet.this);
        yparam.setHint("Y Co-ordinate");
        yparam.setGravity(Gravity.LEFT);
        layout.addView(yparam);

        alertDialog.setView(layout);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mcoordinatorLayout = (CoordinatorLayout) findViewById(R.id.idLayout);
        myDb = new DatabaseHelper(this);

        Intent intent = getIntent();
        intentId = intent.getIntExtra("id", 0);

        displaydata();

        Toast.makeText(getApplicationContext(),"Training Set! ",Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"Item Clicked: "+ intentId,Toast.LENGTH_SHORT).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertBox();

                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"ADD",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String xvalue = xparam.getText().toString().trim();
                        String yvalue = yparam.getText().toString().trim();
                        if(xvalue.equals("") || yvalue.equals("")){
                            Toast.makeText(getApplicationContext(),"Please Fill All Fields", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            int xcoordinate = Integer.parseInt(xvalue);
                            int ycoordinate = Integer.parseInt(yvalue);

                            try {
                                boolean isInserted = myDb.insertDataTraining(xcoordinate, ycoordinate, -9, -8, -7, intentId);
                                if (isInserted == true) {
                                    Intent intentrefresh = getIntent();
                                    intentrefresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intentrefresh);
                                    Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG).show();
                                } else
                                    Toast.makeText(getApplicationContext(), "Error: Could Not Insert Data", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Something was wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                        //myDb.close();
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
