package fi.oulu.interactivestoryeditor;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class PickGPSLocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, LocationListener {

    private TextView tv_latitude;
    private TextView tv_longitude;

    private double latitude;
    private double longitude;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        tv_latitude = (TextView)  findViewById(R.id.tv_latitude);
        tv_longitude = (TextView)  findViewById(R.id.tv_longitude);

        Button save_btn = (Button) findViewById(R.id.gps_pick_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyFields())
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("latitude", latitude);
                    returnIntent.putExtra("longitude", longitude);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        if(getIntent().getBooleanExtra("location",false))
        {
            latitude = getIntent().getDoubleExtra("latitude",0);
            longitude = getIntent().getDoubleExtra("longitude",0);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private boolean verifyFields() {
        if(latitude != 0 || longitude != 0)
        {
            return true;
        }
        Toast.makeText(this,"Please select a place",Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setOnCameraChangeListener(this);

        if(latitude!=0||longitude!=0)
        {
            LatLng latLng = new LatLng(latitude, longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);
        }
        else
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng location = cameraPosition.target;
        latitude = location.latitude;
        longitude = location.longitude;

        tv_latitude.setText((double) Math.round(latitude * 100000) / 100000 + "");
        tv_longitude.setText((double) Math.round(longitude * 100000) / 100000 + "");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap!=null)
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
            locationManager.removeUpdates(this);
        }
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
