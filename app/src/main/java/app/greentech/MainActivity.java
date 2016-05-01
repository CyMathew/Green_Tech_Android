package app.greentech;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import app.greentech.Fragments_Main.*;

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
    Fragment_Social socialFrag;
    Fragment_Links linksFrag;
    Fragment_Faq faqFrag;
    Fragment_Tips tipsFrag;
    Fragment_Settings settingsFrag;

    View header_view;

    ImageView nav_picture;
    TextView nav_TV_user;
    TextView nav_TV_email;
    Button loginButton;

    JSONObject response, profile_pic_data, profile_pic_url;

    final static int REQUEST_LOGIN = 0x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

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

        mapFrag = new Fragment_Map();
        statFrag = new Fragment_Stats();
        socialFrag = new Fragment_Social();
        linksFrag = new Fragment_Links();
        faqFrag = new Fragment_Faq();
        tipsFrag = new Fragment_Tips();
        settingsFrag = new Fragment_Settings();

        fragment = mapFrag;
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

        setNavigationHeader();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //toolbar.setTitle("Map");
        initializeLogin();
    }

    /*
        Set Navigation header by using Layout Inflater.
     */

    public void setNavigationHeader()
    {

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(this);


        loginButton = (Button) findViewById(R.id.btn_start_login);
        nav_picture = (ImageView) findViewById(R.id.nav_profilePic);
        nav_TV_user = (TextView) findViewById(R.id.nav_username);
        nav_TV_email = (TextView) findViewById(R.id.nav_email);
    }

    private void initializeLogin()
    {
        if ((preferences.getBoolean(getString(R.string.is_logged_in), false))) {

            Log.i("Info", "User found to be logged in");
            //changeProfileState(true);
            setUserProfile(preferences.getString("FB_jsondata", ""));

        }
    }


    /*
       Set User Profile Information in Navigation Bar.
    */

    public  void  setUserProfile(String jsondata){

        try {
            response = new JSONObject(jsondata);
            nav_TV_email.setText(response.get("email").toString());
            nav_TV_user.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

            Picasso.with(this).load(profile_pic_url.getString("url")).into(nav_picture);

        } catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //nav_TV_user.setText(preferences.getString("Username", ""));
        //nav_TV_email.setText(preferences.getString("Email", ""));

        //Once logged in, hide login button and show user name, user picture and user account
        switch (requestCode) {

            case REQUEST_LOGIN:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("TAG", "User logged in.");
                        //changeProfileState(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "User chose not to login");
                        break;
                }
                break;
        }

    }

    private void changeProfileState(boolean value)
    {
        if(value)
        {
            Log.i("Info", "Changing Visibility");
            loginButton = (Button) findViewById(R.id.btn_start_login);
            nav_picture = (ImageView) findViewById(R.id.nav_profilePic);
            nav_TV_user = (TextView) findViewById(R.id.nav_username);
            nav_TV_email = (TextView) findViewById(R.id.nav_email);


            loginButton.setVisibility(View.GONE);
            nav_picture.setVisibility(View.VISIBLE);
            nav_TV_email.setVisibility(View.VISIBLE);
            nav_TV_user.setVisibility(View.VISIBLE);

            nav_TV_user.setText(preferences.getString("Username", ""));
            nav_TV_email.setText(preferences.getString("Email", ""));
        }
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
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }
        else {
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
        if (id == R.id.barcode_scan_option) {
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivity(intent);
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
                break;
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLoginClick(View view) {

        Intent intent_Login = new Intent(this, LoginActivity.class);
        startActivityForResult(intent_Login, REQUEST_LOGIN);
    }
}