package app.greentech.Fragments_Main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import app.greentech.R;

/**
 * Created by Cyril on 3/2/16.
 */
public class Fragment_Settings extends PreferenceFragment {

    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());




    }
}

