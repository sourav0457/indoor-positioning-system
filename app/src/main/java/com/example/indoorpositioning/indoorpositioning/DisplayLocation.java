package com.example.indoorpositioning.indoorpositioning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayLocation extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE = 123;
    DatabaseHelper myDb;
    int intentId;
    int rss1,rss2,rss3;
    String mac1, mac2, mac3;
    WifiManager wifi;
    List<ScanResult> wifiList;
    IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    ArrayList<Double> distanceArray = new ArrayList<>();
    ArrayList<Integer> idArray = new ArrayList<>();
    int count;
    double minarray[] = new double[5];
    int minid[] = new int[5];
    double x,y;
    double xarray[] = new double[5];
    double yarray[] = new double[5];
    ImageView img;
    Handler h;
    int delay;

    public void knn(){
        x=((xarray[0]/minarray[0]) + (xarray[1]/minarray[1]) + (xarray[2]/minarray[2]) + (xarray[3]/minarray[3]) + (xarray[4]/minarray[4]))/(1/minarray[0]+1/minarray[1]+1/minarray[2]+1/minarray[3]+1/minarray[4]);
        y=((yarray[0]/minarray[0]) + (yarray[1]/minarray[1]) + (yarray[2]/minarray[2]) + (yarray[3]/minarray[3]) + (yarray[4]/minarray[4]))/(1/minarray[0]+1/minarray[1]+1/minarray[2]+1/minarray[3]+1/minarray[4]);
        //Toast.makeText(getApplicationContext(),"(X: " + x + " Y: " + y + ")",Toast.LENGTH_LONG).show();
    }

    public void findxy(){
        Cursor res;
        for(int i=0;i<5;i++){
            res = myDb.getXYDataTraining(minid[i]);
            res.moveToFirst();
            xarray[i]=res.getInt(1);
            yarray[i]=res.getInt(2);
            res.close();
        }
    }

    public void findmindistance(){
        int i,j,id;
        for(i=0;i<5;i++){
            minarray[i] = distanceArray.get(0);
            minid[i] = idArray.get(0);
            id=0;
            for(j=1;j<distanceArray.size();j++){
                if(distanceArray.get(j)<minarray[i]){
                    minarray[i]=distanceArray.get(j);
                    minid[i]=idArray.get(j);
                    id=j;
                }
            }
            distanceArray.set(id,9999.99);
        }
        //Toast.makeText(getApplicationContext(),"MIN_DIST: " + minarray[0] + " " + minarray[1] + " " + minarray[2] + " " + minarray[3] + " " + minarray[4],Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),"MIN_ID: " + minid[0] + " " + minid[1] + " " + minid[2] + " " + minid[3] + " " + minid[4],Toast.LENGTH_SHORT).show();

    }

    public void finddistance(){
        Cursor res = myDb.getAllDataTraining(intentId);
        if(res.getCount() == 0){
            Toast.makeText(getApplicationContext(),"Add Training data to get started!",Toast.LENGTH_LONG).show();
            return;
        }
        res.moveToFirst();
        do{
            double distance;
            distance = Math.sqrt(Math.pow(rss1-res.getInt(3),2)+Math.pow(rss2-res.getInt(4),2)+Math.pow(rss3-res.getInt(5),2));
            distanceArray.add(distance);
            idArray.add(res.getInt(0));
        }while(res.moveToNext());

        res.close();

    }

    protected void onResume(){
        this.registerReceiver(mWifiScanReceiver,filter);
        super.onResume();
    }

    protected void onPause(){
        this.unregisterReceiver(mWifiScanReceiver);
        super.onPause();
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                //Toast.makeText(getApplicationContext(),"RSS1: " + rss1 +"RSS2: " + rss2 +"RSS3: " + rss3,Toast.LENGTH_LONG).show();
            }
        }
    };

    private void myWifiMethod() {
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    public void getMACAddresses()
    {
        Cursor res = myDb.getDataById(intentId);
        //SHOULD RETURN ONLY 1 ROW
        if(res.getCount() == 0){
            Toast.makeText(getApplicationContext(),"Some Error Occurred!",Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(),"Wifi is now Enabled",Toast.LENGTH_SHORT).show();
        }
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_location);
        img = (ImageView) findViewById(R.id.imageButton);

        turnGPSOn();
        myDb = new DatabaseHelper(this);

        Intent intent = getIntent();
        intentId = intent.getIntExtra("id", 0);
        //Toast.makeText(getApplicationContext(),"Intent id: " + intentId,Toast.LENGTH_SHORT).show();

        getMACAddresses();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        turnWifiOn();

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DisplayLocation.this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE);
        }
        else{
            //Toast.makeText(getApplicationContext(),"Method Called",Toast.LENGTH_SHORT).show();
            myWifiMethod();
        }

        calculate();
    }

    public void calculate(){
        h = new Handler();
        delay = 100;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                wifi.startScan();
                finddistance();
                findmindistance();
                findxy();
                knn();
                img.setX((float)x);
                img.setY((float)y);
                h.postDelayed(this, delay);
            }
        },delay);
    }
}
