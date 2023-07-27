package com.example.mymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btn_screenshot, btn_location;
    private TextView tv_latitude, tv_longitude;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_screenshot = (Button) findViewById(R.id.btn_screenshot);
        btn_location = (Button) findViewById(R.id.btn_location);

        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);

        btn_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to take a screenshot
                takeScreenshot();
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permission before getting the current location
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted, get the location
                    getLocation();
                } else {
                    // Request location permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });

    }

    private void takeScreenshot() {
        // Get the root view of the current activity
        View rootView = getWindow().getDecorView().getRootView();

        // Create a Bitmap and draw the view contents on it
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Now you have the screenshot captured in the 'screenshotBitmap' variable
        // You can save it to a file or share it as needed

        // For example, you can save it to the app-specific external directory
        String screenshotFileName = "screenshot_" + System.currentTimeMillis() + ".png";
        File screenshotFile = new File(getExternalFilesDir(null), screenshotFileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(screenshotFile);
            screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Screenshot saved: " + screenshotFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save the screenshot.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocation() {
        // Get the location using LocationManager or Google Play Services (FusedLocationProviderClient)
        // Here, I'll provide a basic example using LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            // Check if the location permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        // Update TextViews with latitude and longitude
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        tv_latitude.setText("Latitude: " + latitude);
                        tv_longitude.setText("Longitude: " + longitude);
                        Toast.makeText(MainActivity.this, "Location updated.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {}
                };

                // Request location updates
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

            } else {
                // Permission not granted, show a message or handle it gracefully
                Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                getLocation();
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}