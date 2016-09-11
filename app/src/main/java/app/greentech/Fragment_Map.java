package app.greentech;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Map fragment that houses Google Map zoomed into the Texas Tech University campus area
 * Has markers to indicate recycling bin locations loaded through geoJSONs added in the App's resources folder
 *
 * Uses Google Maps, Google Play Utils, FAB SpeedDial
 * @author Cyril Mathew
 */
public class Fragment_Map extends Fragment implements OnMapReadyCallback, OnMarkerClickListener,
                                                        OnInfoWindowClickListener, OnClickListener,
                                                        OnMapClickListener, LocationListener
{

    /**
     * GoogleMap reference
     */
    // Might be null if Google Play services APK is not available.
    private GoogleMap gMap;

    /**
     *geoJSONLayer for easy addition of geoJSON markers
     */
    private GeoJsonLayer binLayer;

    /**
     * Mapview which holds the Google Maps within the fragment
     */
    public static MapView mapView;

    /**
     * ArrayLists to hold marker locations after processing from geoJSON
     */
    private ArrayList<Marker> binList;

    /**
     * Floating Action Button for when a marker is selected
     */
    private FloatingActionButton directionsFab;

    /**
     * Floating Action Button Speedial that normally shows when nothing has been selected
     */
    private FabSpeedDial extrasFab;

    /**
     * Latitude and Longitude for markers
     */
    private LatLng markerPosition;

    /**
     * Current Location of user
     */
    private Location mCurrentLocation;

    /**
     * Used to get location data
     */
    private LocationManager mLocationManager;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 10

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5000; // 2500 milliseconds

    private static final int REQUEST_RECYCLE = 0x1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
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
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}
                    });
        }
        catch (SecurityException e) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.MAPS_RECEIVE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.MAPS_RECEIVE}, MainActivity.PERMISSIONS_REQUEST_MAPS_RECEIVE);
            }
        }

        directionsFab = (FloatingActionButton) v.findViewById(R.id.fab_directions);
        extrasFab = (FabSpeedDial) v.findViewById(R.id.fab_extras);

        extrasFab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                if(menuItem.getTitle().equals("Quick Find"))
                {
                    findNearestLocation();
                }

                else if(menuItem.getTitle().equals("Add Recycling Event"))
                {
                    Intent intent = new Intent(getActivity(), RecycleActivity.class);
                    getActivity().startActivityForResult(intent, REQUEST_RECYCLE);
                }


                return false;
            }
        });


        directionsFab.setOnClickListener(this);
        directionsFab.hide();


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
            binLayer = new GeoJsonLayer(mapView.getMap(), R.raw.bin_geojson, getActivity().getApplicationContext());

        } catch (JSONException e) {
            Log.e("JSON Error", "GEOJSON input was malformed");
        }
        catch (IOException e)
        {
            Log.e("IO Error", "Couldn't find/use GEOJSON file");
        }

        addMarkers(binLayer);

        // Set a listener for info window events.
        gMap.setOnInfoWindowClickListener(this);
        gMap.setOnMarkerClickListener(this);

        gMap.setOnMapClickListener(this);

        try{
            gMap.setMyLocationEnabled(true);}
        catch (SecurityException e)
        {
            //If location permission hasn't been given, let user know
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(), "Location permissions are lacking", Toast.LENGTH_SHORT).show();
        }

        //default camera location
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.586513, -101.883885), 14));
    }


    /**
     * Adds markers to the map for each geoJSON layer
     * @param bLayer
     */
    public void addMarkers(GeoJsonLayer bLayer)
    {
        for (GeoJsonFeature feature : bLayer.getFeatures())
        {
            binList.add(gMap.addMarker(new MarkerOptions()
                    .position(((GeoJsonPoint)feature.getGeometry()).getCoordinates())
                    .title(feature.getProperty("name"))
                    .snippet(feature.getProperty("building"))));
        }
    }


    /**
     * Finds the nearest recycling marker to the user's current location for quick access
     */
    private void findNearestLocation()
    {
        //Set parameters
        float shortestDistance = 10000, tmpDistance;
        int index = 0;

        if(mCurrentLocation != null)        //If current location data is available
        {

            for (int i = 0; i < binList.size(); i++)            //Take all recycling bin locations and calculate the min distance to one
            {
                Location targetLocation = new Location("");
                targetLocation.setLatitude(binList.get(i).getPosition().latitude);
                targetLocation.setLongitude(binList.get(i).getPosition().longitude);

                tmpDistance = mCurrentLocation.distanceTo(targetLocation);

                if (tmpDistance < shortestDistance) {
                    shortestDistance = tmpDistance;
                    index = i;
                }
            }

            //Animate the map camera to the marker to point out to the user
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(binList.get(index).getPosition(), 18));

            //Show the info window of marker for more info
            binList.get(index).showInfoWindow();
        }

        else                                    //If current location data is unavailable
        {
            //Let the user know that the current location data is not yet ready
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
    public void onInfoWindowClick(Marker marker) { }

    /**
     * When a marker is clicked, show the info window
     * @param marker
     * @return the event is never consumed so false is returned
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        gMap.getUiSettings().setMapToolbarEnabled(false);
        markerPosition = marker.getPosition();
        extrasFab.hide();
        directionsFab.show();

        return false;
    }

    /**
     * When the Directions Floating Action Button is clicked after selecting a marker, the App will
     * open Google Maps and input the marker position for directions to the marker from current location
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        //Set the URI using selected markerPosition
        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + markerPosition.latitude + "," + markerPosition.longitude);

        //Open Google Maps using URI as the intent
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    /**
     * When the user clicks away from a marker, the Floating Action Button changes to the FAB Speed Dial
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        directionsFab.hide();
        extrasFab.show();
    }

    /**
     * Override method used to keep track of when location has changed
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {}
}
