package com.aslan.personalguardianapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.aslan.personalguardianapp.R;
import com.aslan.personalguardianapp.logic.LocationReceiver;
import com.aslan.personalguardianapp.logic.OnLocationChangedListener;
import com.aslan.personalguardianapp.util.Constants;
import com.aslan.personalguardianapp.util.Utility;
import com.aslan.personalguardianapp.util.XMLreader;


/**
 * The SPLASH ACTIVITY of the application Will check and enable internet
 * connectivity, location services Will get the country of the user to improve
 * search results
 *
 * @author Vishnuvathsasarma
 */
public class SplashActivity extends Activity {

    private String country;
    private XMLreader locationReader;
    private Location location;
    private LocationReceiver locationReceiver;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Service.LOCATION_SERVICE);
        locationReceiver = new LocationReceiver(this, 10, 1000);
        if (isLocationServiceAvailable()) {
            if (isNetworkAvailable()) {

                locationReceiver
                        .setOnLocationChangedListener(new OnLocationChangedListener() {

                            @Override
                            public void onLocationChanged(Location loc) {
                                location = loc;
                            }
                        });
                locationReceiver.start();

                if (location == null) {
                    // GPS should be enabled to obtain current location if the
                    // device does not contain a last known location
                    askToEnableGPSservice();
                } else {
                    getCountry();
                }
            } else {
                askToEnableNetwork();
            }
        } else {
            askToEnableLocationService();
        }
    }

    private void getCountry() {
        // method to get the country of the user
        double latitude = location.getLatitude(); // 20.593;
        double longitude = location.getLongitude(); // 78.962;

        String currLoc = latitude + "," + longitude;
        String URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng="
                + currLoc
                + "&result_type=country&key=AIzaSyCyc9xJr_8wXxrmjeadKVhpVp84nkleoyE";
        locationReader = new XMLreader();
        locationReader.setTAG("formatted_address");
        new XmlReader().execute(URL);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    // Check Internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Show alert dialog to confirm and enable the network
    private void askToEnableNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_request_msg)
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Check LocationService
    private boolean isLocationServiceAvailable() {
        // Log.i("&%$^#%^&#%&#%^#$%#^&", "iuyfbuytf");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Show alert dialog to confirm and enable the LocationService
    private void askToEnableLocationService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.location_service_request_msg)
                .setTitle("Unable to detect location")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askToEnableGPSservice() {
        // Show alert dialog to confirm and enable the GPS service
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.gps_request_msg)
                    .setTitle("Unable to detect location")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent i = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(i);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private class XmlReader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String temp = locationReader.readURL(urls[0]);
            return temp;
        }

        @Override
        protected void onPostExecute(String result) {
            country = result;
            if (country != null && country.length() > 0) {
                boolean firstRun = Utility.isFirstRun(getApplicationContext());
                Log.i("Splash Activity", "" + firstRun);
                Class target;
                // TODO: Check first run and change the activity
                if (firstRun) {
                    target = RegisterActivity.class;
                } else if (Utility.isUserSignedIn(getApplicationContext()).equals(Constants.SIGNED_OUT)) {
                    //TODO should be login activity
                    target = RegisterActivity.class;
                } else {
                    target = MainActivity.class;
                }
                Intent intent = new Intent(SplashActivity.this, target);
                intent.putExtra("Country", country);
                intent.putExtra("Lat", location.getLatitude());
                intent.putExtra("Lon", location.getLongitude());

                locationReceiver.stop();

                startActivity(intent);

            } else {
                // invoked when no data received due to error in internet
                // connection
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SplashActivity.this);
                builder.setMessage(R.string.internet_error_msg)
                        .setTitle("Unable to retrive data from internet")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        SplashActivity.this.finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }
}