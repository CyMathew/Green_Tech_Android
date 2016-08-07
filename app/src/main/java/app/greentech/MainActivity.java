package app.greentech;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * MainActivity of GreenTech Recycling App. Houses all the fragments that switched to/from using the Navigation Drawer
 * @author Cyril Mathew
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Used to set the title of the Activity in the app.
     */
    private Toolbar toolbar;

    /**
     * Used to interact with the Navigation Drawer layout itself
     */
    private DrawerLayout drawer;

    /**
     * The view that houses the drawer layout for the Navigation Drawer
     */
    private NavigationView navigationView;

    /**
     * Declaration of user preferences object
     */
    private SharedPreferences preferences;

    /**
     * Used to manage and switch between fragments
     */
    private FragmentManager fragmentManager;

    /**
     * Declaration of fragment reference object
     */
    private Fragment fragment;

    /**
     * Declaration of all of the different fragment types available in the app,
     * in order to switch between them easily
     */
    private Fragment_Map mapFrag;
    private Fragment_Stats statFrag;
    private Fragment_Links linksFrag;
    private Fragment_Faq faqFrag;
    private Fragment_Tips tipsFrag;

    /**
     * Constant as flag to determine if it was RecycleActivity that finished
     */
    private final static int REQUEST_RECYCLE = 0x1;

    /**
     * Public declaration of database connection to be used with other classes and activities.
     */
    public static StatsDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the default shared preferences file for app.
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Declaration of a new database connection
        dataSource = new StatsDataSource(this);

        //Set reference to toolbar and make visible in app
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set reference to navigation view and start listening for actions
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set references for the drawer layout within the Navigation view
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Toolbar button that indicates to the user that there is a drawer and allows for access simply by clicking it
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Declare all fragments once MainActivity has been set up
        mapFrag = new Fragment_Map();
        statFrag = new Fragment_Stats();
        linksFrag = new Fragment_Links();
        faqFrag = new Fragment_Faq();
        tipsFrag = new Fragment_Tips();

        //Set the initial fragment to be the map
        fragment = mapFrag;
        fragmentManager = getFragmentManager();
        setFragment(fragment);

    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!RecycleActivity.active)
            dataSource.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            //If RecycleActivity just finished
            case REQUEST_RECYCLE:
                switch (resultCode)
                {
                    //If Input was entered properly
                    case Activity.RESULT_OK:
                        Log.i("Action", "User added to stats");
                        dataSource.addToStats(data.getStringExtra("Selection"));
                        break;
                    //If RecycleActivity was backed away from or canceled
                    case Activity.RESULT_CANCELED:
                        Log.i("Action", "User chose not to add to stats");
                        break;
                }
                break;
        }

    }

    /**
     * Simple method that sets the fragment within MainActivity.
     * Used as a method as the functionality is used repeatedly
     * @param fragment
     */
    private void setFragment(Fragment fragment)
    {
        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_canvas, fragment)
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))   //If the drawer is open when back is pressed, close the drawer
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fragment != mapFrag)                    //If the fragment is not map when back is pressed, switch to map
        {
            toolbar.setTitle("Map");
            fragment = mapFrag;
            setFragment(fragment);
        }
        else                                            //Else, accept the back press properly
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id)
        {
            case R.id.nav_stats:
                toolbar.setTitle("Statistics");
                fragment = statFrag;
                break;
            case R.id.nav_map:
                toolbar.setTitle("Map");
                fragment = mapFrag;
                break;
            case R.id.nav_links:
                toolbar.setTitle("Links");
                fragment = linksFrag;
                break;
            case R.id.nav_faq:
                toolbar.setTitle("FAQs");
                fragment = faqFrag;
                break;
            case R.id.nav_tips:
                toolbar.setTitle("Tips");
                fragment = tipsFrag;
                break;
            default:
                break;
        }

        setFragment(fragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}