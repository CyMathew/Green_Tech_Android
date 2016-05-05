package app.greentech;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    View nav_headerView;

    ImageView nav_picture;
    TextView nav_TV_user;
    TextView nav_TV_email;
    Button nav_loginButton;

    JSONObject response, profile_pic_data, profile_pic_url;

    final static int REQUEST_LOGIN = 0x1;
    final static int TYPE_FB = 0x1;
    final static int TYPE_GOOG = 0x2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_headerView = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(nav_headerView);

        nav_loginButton = (Button) nav_headerView.findViewById(R.id.btn_start_login);
        nav_picture = (ImageView) nav_headerView.findViewById(R.id.nav_profilePic);
        nav_TV_user = (TextView) nav_headerView.findViewById(R.id.nav_username);
        nav_TV_email = (TextView) nav_headerView.findViewById(R.id.nav_email);

        initializeLogin();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //initializeLogin();

        mapFrag = new Fragment_Map();
        statFrag = new Fragment_Stats();
        socialFrag = new Fragment_Social();
        linksFrag = new Fragment_Links();
        faqFrag = new Fragment_Faq();
        tipsFrag = new Fragment_Tips();
        settingsFrag = new Fragment_Settings();

        fragment = mapFrag;
        fragmentManager = getFragmentManager();
        setFragment(fragment);


        //toolbar.setTitle("Map");
        //
    }

    private void initializeLogin()
    {
        if ((preferences.getBoolean(getString(R.string.is_logged_in), false))) {

            Log.i("Info", "User is logged in");
            switch(preferences.getInt("AccountType", 0))
            {
                case TYPE_FB:
                                setUserProfile(preferences.getString("FB_jsondata", ""), TYPE_FB);
                                break;
                case TYPE_GOOG:
                                setUserProfile(preferences.getString("GUsername", ""), TYPE_GOOG);
                                break;
            }
        }
    }


    /*
       Set User Profile Information in Navigation Bar.
    */

    public void setUserProfile(String data, int type)
    {
        changeProfileState(true);

        switch(type)
        {
            case TYPE_FB:
                try
                {
                    response = new JSONObject(data);
                    String user_name = response.get("name").toString();
                    String user_account = response.get("email").toString();
                    nav_TV_email.setText(user_account);
                    nav_TV_user.setText(user_name);

                    SharedPreferences.Editor prefEdit = preferences.edit();
                    prefEdit.putString("Username", user_name);
                    prefEdit.putString("Account", user_account);
                    prefEdit.commit();

                    boolean b = response.getBoolean("installed");
                    if(b) {Log.i("FRIEND!", response.getString("name"));}

                    profile_pic_data = new JSONObject(response.get("picture").toString());
                    profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

                    Picasso.with(this).load(profile_pic_url.getString("url")).into(nav_picture);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case TYPE_GOOG:
                nav_TV_email.setText(data.toString());
                //Log.i("Google Login Info", data.toString());
                break;
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
                        switch(preferences.getInt("AccountType", 0x0))
                        {
                            case TYPE_FB:
                                Log.i("Info", "Logging into Facebook");
                                setUserProfile(preferences.getString("FB_jsondata", ""), TYPE_FB);
                                break;
                            case TYPE_GOOG:
                                Log.i("Info", "Logging into Google+");
                                setUserProfile(preferences.getString("GUsername", ""), TYPE_GOOG);
                                break;
                        }
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

            nav_loginButton.setVisibility(View.GONE);
            nav_picture.setVisibility(View.VISIBLE);
            nav_TV_email.setVisibility(View.VISIBLE);
            nav_TV_user.setVisibility(View.VISIBLE);
        }
        else
        {
            nav_loginButton.setVisibility(View.VISIBLE);
            nav_picture.setVisibility(View.INVISIBLE);
            nav_TV_email.setVisibility(View.VISIBLE);
            //TODO: CHANGE VISIBILITY
            nav_TV_user.setVisibility(View.INVISIBLE);
        }
    }

    private void setFragment(Fragment fragment)
    {
        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_canvas, fragment)
                .commit();
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

        setFragment(fragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLoginClick(View view) {

        Intent intent_Login = new Intent(this, LoginActivity.class);
        startActivityForResult(intent_Login, REQUEST_LOGIN);
    }
}