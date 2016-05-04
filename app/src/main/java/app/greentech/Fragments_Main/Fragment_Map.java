package app.greentech.Fragments_Main;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;

import app.greentech.R;

/**
 * Created by Cyril on 3/3/16.
 */
public class Fragment_Map extends Fragment implements OnMapReadyCallback, OnMarkerClickListener, OnInfoWindowClickListener {

    private GoogleMap gMap; // Might be null if Google Play services APK is not available.
    private GeoJsonLayer waterLayer, binLayer;
    public static MapView mapView;


    //TODO: Add all recycling bin markers
    //TODO: Add descriptive info to info boxes on markers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        try {
            // Changing map type
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Enable / Disable zooming controls
            gMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            gMap.getUiSettings().setMyLocationButtonEnabled(true);


            // Enable / Disable Compass icon
            gMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            gMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            gMap.getUiSettings().setZoomGesturesEnabled(true);

            MapsInitializer.initialize(this.getActivity());

        } catch (Exception e) {
            System.out.println(e);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;

        try {
            waterLayer = new GeoJsonLayer(mapView.getMap(), R.raw.water_geojson, getActivity().getApplicationContext());
            binLayer = new GeoJsonLayer(mapView.getMap(), R.raw.bin_geojson, getActivity().getApplicationContext());

        } catch (Exception e) {
            //TODO: Handle the geoJSON exception

        }

        waterLayer.addLayerToMap();
        binLayer.addLayerToMap();
        //gMap.addMarker(new MarkerOptions().position(new LatLng(33.586513, -101.883885))).setTitle("First Marker");

        // Set a listener for info window events.
        gMap.setOnInfoWindowClickListener(this);

        gMap.setOnMarkerClickListener(this);

        try{
            gMap.setMyLocationEnabled(true);}
        catch (SecurityException e) {e.printStackTrace();}

        //default camera location
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.586513, -101.883885), 14));

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

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("Info", "You clicked info window!");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for (GeoJsonFeature feature : waterLayer.getFeatures()) {
            if (feature.getId().equals(marker.getId())) {
                Log.i("Info", feature.getId());
                marker.setTitle(feature.getProperty("name"));
                marker.setSnippet(feature.getProperty("building"));
            }
        }

        return false;
    }


}
