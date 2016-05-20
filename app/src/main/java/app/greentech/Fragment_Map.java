package app.greentech;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;

import java.util.ArrayList;

import app.greentech.R;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by Cyril on 3/3/16.
 */
public class Fragment_Map extends Fragment implements
        OnMapReadyCallback, OnMarkerClickListener,
        OnInfoWindowClickListener, OnCheckedChangeListener,
        OnClickListener, OnMapClickListener,
        LocationListener{

    private GoogleMap gMap; // Might be null if Google Play services APK is not available.
    private GeoJsonLayer waterLayer, binLayer;
    public static MapView mapView;

    private ArrayList<Marker> waterList, binList;
    private CheckBox binFilter, waterFilter;
    private FloatingActionButton directionsFab;
    private FabSpeedDial extrasFab;
    private LinearLayout filters;
    private boolean binMarkVisible, waterMarkVisible;
    private LatLng markerPosition;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 10

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5000; // 2500 milliseconds


    //TODO: Add all recycling bin markers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        waterList = new ArrayList<Marker>();
        binList = new ArrayList<Marker>();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, new android.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d("Location Update", "CHANGED");
                            mCurrentLocation = location;
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

        directionsFab = (FloatingActionButton) v.findViewById(R.id.fab_directions);
        extrasFab = (FabSpeedDial) v.findViewById(R.id.fab_extras);
        filters = (LinearLayout) v.findViewById(R.id.layout_checkbox);
        binFilter = (CheckBox) filters.findViewById(R.id.toggle_bins);
        waterFilter = (CheckBox) filters.findViewById(R.id.toggle_water);

        extrasFab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                if(menuItem.getTitle().equals("Filters"))
                {
                    if(filters.getVisibility() == View.VISIBLE) {
                        filters.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        filters.setVisibility(View.VISIBLE);
                    }
                }

                else if(menuItem.getTitle().equals("Quick Find"))
                {
                    findNearestLocation();
                }


                return false;
            }
        });

        binMarkVisible = true;
        waterMarkVisible = true;

        directionsFab.setOnClickListener(this);
        directionsFab.hide();

        binFilter.setOnCheckedChangeListener(this);
        waterFilter.setOnCheckedChangeListener(this);

        try {
            // Changing map type
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Enable / Disable zooming controls
            gMap.getUiSettings().setZoomControlsEnabled(false);
            gMap.getUiSettings().setMapToolbarEnabled(false);

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

        addMarkers(waterLayer, binLayer);

        // Set a listener for info window events.
        gMap.setOnInfoWindowClickListener(this);
        gMap.setOnMarkerClickListener(this);

        gMap.setOnMapClickListener(this);

        try{
            gMap.setMyLocationEnabled(true);}
        catch (SecurityException e) {e.printStackTrace();}

        //default camera location
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.586513, -101.883885), 14));
    }

    public void addMarkers(GeoJsonLayer wLayer, GeoJsonLayer bLayer)
    {
        for (GeoJsonFeature feature : wLayer.getFeatures())
        {
                waterList.add(gMap.addMarker(new MarkerOptions()
                        .position(((GeoJsonPoint)feature.getGeometry()).getCoordinates())
                        .title(feature.getProperty("name"))
                        .snippet(feature.getProperty("building"))));
        }

        for (GeoJsonFeature feature : bLayer.getFeatures())
        {
            binList.add(gMap.addMarker(new MarkerOptions()
                    .position(((GeoJsonPoint)feature.getGeometry()).getCoordinates())
                    .title(feature.getProperty("name"))
                    .snippet(feature.getProperty("building"))));
        }
    }

    public void showMarkers(ArrayList<Marker> list)
    {
        for(Marker m: list)
        {
            m.setVisible(true);
        }
    }

    public void hideMarkers(ArrayList<Marker> list)
    {
        for(Marker m: list)
        {
            m.setVisible(false);
        }
    }

    private void findNearestLocation()
    {
        float shortestDistance = 10000, tmpDistance;
        int index = 0;

        if(mCurrentLocation != null) {

            for (int i = 0; i < binList.size(); i++) {
                Location targetLocation = new Location("");
                targetLocation.setLatitude(binList.get(i).getPosition().latitude);
                targetLocation.setLongitude(binList.get(i).getPosition().longitude);

                tmpDistance = mCurrentLocation.distanceTo(targetLocation);

                if (tmpDistance < shortestDistance) {
                    shortestDistance = tmpDistance;
                    index = i;
                }
            }

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(binList.get(index).getPosition(), 18));
            binList.get(index).showInfoWindow();
        }

        else
        {
            Toast.makeText(getActivity(), "Location not yet ready", Toast.LENGTH_SHORT).show();
        }




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

        gMap.getUiSettings().setMapToolbarEnabled(false);
        markerPosition = marker.getPosition();
        extrasFab.hide();
        directionsFab.show();

        return false;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(buttonView == binFilter)
        {
            if(binMarkVisible)
            {
                hideMarkers(binList);
                binMarkVisible = false;

            }
            else
            {
                showMarkers(binList);
                binMarkVisible = true;

            }
        }

        else if(buttonView == waterFilter)
        {
            if(waterMarkVisible)
            {
                hideMarkers(waterList);
                waterMarkVisible = false;
            }
            else
            {
                showMarkers(waterList);
                waterMarkVisible = true;
            }
        }

    }

    @Override
    public void onClick(View v) {

        //Log.i("Marker", markerPosition.toString());

        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + markerPosition.latitude + "," + markerPosition.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);



    }

    @Override
    public void onMapClick(LatLng latLng) {
        directionsFab.hide();
        extrasFab.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
