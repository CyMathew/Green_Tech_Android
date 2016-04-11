package app.greentech;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    Fragment fragment;

    MapFragment mapFrag;
    StatsFragment statFrag;
    SocialFragment socialFrag;
    LinksFragment linksFrag;
    FaqFragment faqFrag;
    TipsFragment tipsFrag;
    SettingsFragment settingsFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        mapFrag = new MapFragment();
        statFrag = new StatsFragment();
        socialFrag = new SocialFragment();
        linksFrag = new LinksFragment();
        faqFrag = new FaqFragment();
        tipsFrag = new TipsFragment();
        settingsFrag = new SettingsFragment();

        fragment = mapFrag;
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //toolbar.setTitle("Map");

        login();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    public void login()
    {
        Intent intent_Login = new Intent(this, LoginActivity.class);
        startActivity(intent_Login);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
            case R.id.nav_social:
                toolbar.setTitle("Social");
                fragment = socialFrag;
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
            case R.id.nav_settings:
                toolbar.setTitle("Settings");
                fragment = settingsFrag;
                break;
            default:
                Log.i("Info", "How did you get here?!");

        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}