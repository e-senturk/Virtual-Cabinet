package tr.edu.yildiz.virtualcabinet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tr.edu.yildiz.virtualcabinet.databinding.ActivityMapsBinding;
import tr.edu.yildiz.virtualcabinet.service.Tools;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private Address currentAddress;
    private ConstraintLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tr.edu.yildiz.virtualcabinet.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.select_a_location);
        Intent intent = getIntent();
        currentAddress = intent.getParcelableExtra("address");
        parentLayout = findViewById(R.id.mapsConstraintLayout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Add a marker in Sydney and move the camera
        if (currentAddress != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude()), 15));
            mMap.addMarker(new MarkerOptions().position(new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude())));
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.015137, 28.979530), 15));
                }
            }
        }
        mMap.setOnMapLongClickListener(this);
    }

    public void returnOriginalLocation(View view) {
        currentAddress = null;
        this.onMapReady(mMap);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0 && addressList.get(0).getThoroughfare() != null) {
                Intent data = new Intent();
                data.putExtra("address", addressList.get(0));
                this.setResult(RESULT_OK, data);
                finish();
            } else {
                mMap.addMarker(new MarkerOptions().position(latLng));
                Tools.showSnackBar(getString(R.string.cant_get_location_information),
                        parentLayout, this, Snackbar.LENGTH_SHORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}