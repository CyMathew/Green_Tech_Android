package app.greentech;

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Cyril on 3/3/16.
 */
public class MapFragment extends Fragment {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public static MapView mapView;

    //TODO: Add all recycling bin markers
    //TODO: Add water refilling stations
    //TODO: Add descriptive info to info boxes on markers

    //TODO: Use newer getMapAsync() rather than dep getMap()
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map,
                container, false);
        try
        {
            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) v.findViewById(R.id.mapview);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            // Gets to GoogleMap from the MapView and does initialization stuff
            mMap = mapView.getMap();
            // Changing map type
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            try{
                mMap.setMyLocationEnabled(false);}
            catch (SecurityException e) {};


            // Enable / Disable zooming controls
            mMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            mMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            MapsInitializer.initialize(this.getActivity());

            setupMap();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return v;
    }

    private void setupMap()
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(33.586513, -101.883885))).setTitle("First Marker");

        //default camera location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.586513, -101.883885), 14));

        //TODO: dynamic camera location/zoom based on user location
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
