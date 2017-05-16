package com.example.indoorpositioning.indoorpositioning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AccessPoints extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE = 123;
    TextView varTxt;
    WifiManager wifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
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

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"Broadcast Receiver working",Toast.LENGTH_SHORT).show();
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                wifiList = wifi.getScanResults();
                sb = new StringBuilder();
                sb.append("\n" + "  Number of Access Points available: " + wifiList.size() + "\n\n");
                for(int i=0; i < wifiList.size(); i++){
                    sb.append("\t"+new Integer(i+1).toString()+ ".");
                    sb.append("Name: "+(wifiList.get(i).SSID).toString()+"\n");
                    sb.append("\t\t\t MAC Address: "+(wifiList.get(i).BSSID).toString()+"\n");
                    sb.append("\t\t\t RSS Value: "+(wifiList.get(i).level)+"\n");
                    sb.append("\t\t\t Frequency: "+(wifiList.get(i).frequency)+"\n");
                    sb.append("\n\n");
                }
                varTxt.setText(sb);

            }
        }
    };

    public void turnWifiOn()
    {

        if(wifi.isWifiEnabled()==false){
            wifi.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(),"Wifi is now Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_points);
        varTxt=(TextView)findViewById(R.id.textView);
        varTxt.setMovementMethod(new ScrollingMovementMethod());
        turnGPSOn();

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        turnWifiOn();

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AccessPoints.this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CHANGE_WIFI_STATE);
        }
        else{
            myWifiMethod();
        }


    }
}
