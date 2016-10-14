package com.ganna.uber.ui.main;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ganna.uber.Constants;
import com.ganna.uber.R;
import com.ganna.uber.communication.FireManager;
import com.ganna.uber.util.MapUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;

import static com.ganna.uber.R.id.map;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LatLng pickedLatlng ;
    private boolean isWaitingForPick;
    private boolean isNavedToDriverFirstTime = true;
    private Marker driverMarker;
    private Marker pickMarker;
    private Criteria locCritria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locCritria = new Criteria();
        locCritria.setAccuracy(Criteria.ACCURACY_FINE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        getRideStatus();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return getLayoutInflater().inflate(R.layout.marker_info, null);
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                pickMarker.setVisible(false);
                isWaitingForPick=true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isWaitingForPick) {
                    pickedLatlng = latLng;
                    pickMarker.setPosition(latLng);
                    pickMarker.setVisible(true);
                    isWaitingForPick=false;
                }
            }
        });
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                showFareDialog();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            mMap.setMyLocationEnabled(true);
            mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(locCritria, false), 1000,
                    0, mLocationListener);

        }
    }

    private void addDefaultmarker() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLocation(),18));
        pickMarker= mMap.addMarker(new MarkerOptions().position(getCurrentLocation()));
        pickedLatlng = getCurrentLocation();
    }

    private void showFareDialog() {
        DialogFragment dialogFragment = new FareDialog();
        Bundle args = new Bundle();
        args.putDouble("lat", pickedLatlng.latitude);
        args.putDouble("lng", pickedLatlng.longitude);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "Fare");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crit, false), 1000,
                    0, mLocationListener);

        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            Log.d("Location", "onLocationChanged: " + location.getLatitude());


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void notifyRider(){
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    long[] pattern = {500,500,500,500,500,500};
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Uber!")
                .setVibrate(pattern)
                .setSound(alarmSound)
                .setContentText("Your uber is coming!")
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void getRideStatus() {
        FireManager.getRideNode()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = (String) dataSnapshot.child(Constants.RIDE_STATUS_KEY).getValue();
                        switch (status) {
                            case Constants.STATUS_PICK_REQUESTED:
                                pickMarker.remove();
                                return;
                            case Constants.STATUS_PICK_ACCEPTED:
                                if (dataSnapshot.child(Constants.DRIVER_LATLNG_KEY).getValue() == null) {
                                    return;
                                }
                                Double lat = (Double) dataSnapshot.child(Constants.DRIVER_LATLNG_KEY).child("lat").getValue();
                                Double lng = (Double) dataSnapshot.child(Constants.DRIVER_LATLNG_KEY).child("lng").getValue();
                                showDriverLocation(new LatLng(lat, lng));
                                return;
                            case Constants.STATUS_PICK_STARTED:
                                Toast.makeText(MainScreen.this, "Trip has started !", Toast.LENGTH_SHORT).show();
                                return;
                            case Constants.STATUS_PICK_ARRIVED:
                                Toast.makeText(MainScreen.this, "Driver has arrived !", Toast.LENGTH_SHORT).show();
                                return;
                            default: //SESSION CLEARED
                                isNavedToDriverFirstTime=true;
                                mMap.clear();
                                addDefaultmarker();


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void showDriverLocation(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        Log.d("Bool", "isNavedToDriverFirstTime: "+isNavedToDriverFirstTime);
        if (isNavedToDriverFirstTime) {
            notifyRider();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_car);
            driverMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(icon));
            isNavedToDriverFirstTime = false;
            return;
        }
        MapUtil.animateMarker(driverMarker,mMap, latLng, false);
    }




    private LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new LatLng(Constants.INITIAL_LAT,Constants.INITIAL_LNG);
        }
        Location oldLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(locCritria, false));
        if (oldLocation!=null){
            return new LatLng(oldLocation.getLatitude(),oldLocation.getLongitude());
        }
        return new LatLng(Constants.INITIAL_LAT,Constants.INITIAL_LNG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FireManager.getRideNode()
                .child(Constants.RIDE_STATUS_KEY).setValue("");
        return true;
    }
}
