package app.greentech;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import app.greentech.Fragments_Login.Fragment_SignIn;

/**
 * Created by Cyril on 4/16/2016.
 */
public class LoginActivity extends AppCompatActivity
{
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);

        Fragment fragment = new Fragment_SignIn();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_login, fragment);
        ft.commit();
    }

}
