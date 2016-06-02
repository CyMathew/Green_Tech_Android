package app.greentech;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    SharedPreferences preferences;

    FragmentManager fragmentManager;
    Fragment fragment;
    Fragment_Map mapFrag;
    Fragment_Stats statFrag;
    Fragment_Links linksFrag;
    Fragment_Faq faqFrag;
    Fragment_Tips tipsFrag;
    Fragment_Settings settingsFrag;

    View nav_headerView;


    private final static int REQUEST_RECYCLE = 0x1;

    public static StatsDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataSource = new StatsDataSource(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //nav_headerView = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        //navigationView.addHeaderView(nav_headerView);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //initializeLogin();

        mapFrag = new Fragment_Map();
        statFrag = new Fragment_Stats();
        linksFrag = new Fragment_Links();
        faqFrag = new Fragment_Faq();
        tipsFrag = new Fragment_Tips();
        settingsFrag = new Fragment_Settings();

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

            case REQUEST_RECYCLE:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Log.i("Action", "User added to stats");
                        addToStats(data.getStringExtra("Selection"));

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("Action", "User chose not to add to stats");
                        break;
                }
                break;
        }

    }


    private void setFragment(Fragment fragment)
    {
        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_canvas, fragment)
                .commit();
    }

    private void addToStats(String selection)
    {
        dataSource.add(selection);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fragment != mapFrag)
        {
            toolbar.setTitle("Map");
            fragment = mapFrag;
            setFragment(fragment);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.barcode_scan_option) {

        }

        return super.onOptionsItemSelected(item);
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
            /*case R.id.nav_settings:
                toolbar.setTitle("Settings");
                fragment = settingsFrag;
                break;*/
            default:
                Log.i("Info", "How did you get here?!");
                break;
        }

        setFragment(fragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}