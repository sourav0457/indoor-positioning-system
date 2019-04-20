package com.example.indoorpositioning.indoorpositioning;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import static android.content.Context.WIFI_SERVICE;


public class TrainingSet extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE = 123;
    CoordinatorLayout mcoordinatorLayout;
    DatabaseHelper myDb;
    GridView gv;
    AlertDialog alertDialog;
    EditText xparam;
    EditText yparam;
    int intentId;
    int rss1,rss2,rss3;
    String mac1, mac2, mac3;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    WifiManager wifi;
    IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    public void turnGPSOn() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected void onResume(){
        this.registerReceiver(mWifiScanReceiver,filter);
        super.onResume();
    }

    protected void onPause(){
        this.unregisterReceiver(mWifiScanReceiver);
        super.onPause();
    }

    private void myWifiMethod() {
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myWifiMethod();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Permission Denied!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getMACAddresses()
    {
        Cursor res = myDb.getDataById(intentId);
        //SHOULD RETURN ONLY 1 ROW
        if(res.getCount() == 0){
            Snackbar snackbar = Snackbar.make(mcoordinatorLayout,"Some Error Occurred!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        else
        {
            res.moveToFirst();
            mac1 = res.getString(2);
            mac2 = res.getString(3);
            mac3 = res.getString(4);
            //Toast.makeText(getApplicationContext(),"Successfully obtained mac addresses from table",Toast.LENGTH_SHORT).show();

        }
    }

    public void turnWifiOn()
    {

        if(wifi.isWifiEnabled()==false){
            wifi.setWifiEnabled(true);
            //Toast.makeText(getApplicationContext(),"Wifi is now Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getApplicationContext(),"Broadcast Receiver working",Toast.LENGTH_SHORT).show();
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                wifiList = wifi.getScanResults();
                for(int i=0; i < wifiList.size(); i++){
                    if(((wifiList.get(i).BSSID).toString()).equals(mac1))
                        rss1 = wifiList.get(i).level;
                    if(((wifiList.get(i).BSSID).toString()).equals(mac2))
                        rss2 = wifiList.get(i).level;
                    if(((wifiList.get(i).BSSID).toString()).equals(mac3))
                        rss3 = wifiList.get(i).level;
                }

            }
        }
    };

    public void displaydata(){
        //Cursor res = myDb.getXYDataTraining(intentId);
        Cursor res = myDb.getAllDataTraining(intentId);
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
                buffer.add("( X : " + res.getFloat(1) + ", Y : " + res.getFloat(2) + " ) (" + res.getInt(3) + "," + res.getInt(4) + "," + res.getInt(5) + ")");
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
                //Toast.makeText(getApplicationContext(),"Item Clicked: "+i,Toast.LENGTH_SHORT).show();
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
        turnGPSOn();
        mcoordinatorLayout = (CoordinatorLayout) findViewById(R.id.idLayout);
        myDb = new DatabaseHelper(this);

        Intent intent = getIntent();
        intentId = intent.getIntExtra("id", 0);

        getMACAddresses();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        turnWifiOn();

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(TrainingSet.this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE);
        }
        else{
            //Toast.makeText(getApplicationContext(),"Method Called",Toast.LENGTH_SHORT).show();
            myWifiMethod();
        }

        displaydata();

        //Toast.makeText(getApplicationContext(),"Training Set! ",Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),"Item Clicked: "+ intentId,Toast.LENGTH_SHORT).show();


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
                            float xparam = (float)(xcoordinate/0.296875)+465;
                            float yparam = (float)(ycoordinate/0.316877153)+14;

                            try {
                                boolean isInserted = myDb.insertDataTraining(xparam, yparam, rss1, rss2, rss3, intentId);
                                if (isInserted == true) {
                                    Intent intentrefresh = getIntent();
                                    intentrefresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intentrefresh);
                                    Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
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
